package br.com.joaomu.service;

import br.com.joaomu.config.RabbitMQConfig;
import br.com.joaomu.dto.DespesaFiltroRequest;
import br.com.joaomu.dto.ExportJobEvent;
import br.com.joaomu.entity.ExportJob;
import br.com.joaomu.entity.Usuario;
import br.com.joaomu.entity.enums.StatusJob;
import br.com.joaomu.entity.enums.TipoExportacao;
import br.com.joaomu.entity.Despesa;
import br.com.joaomu.entity.enums.CategoriaDespesa;
import br.com.joaomu.entity.enums.StatusDespesa;
import br.com.joaomu.repository.DespesaRepository;
import br.com.joaomu.repository.ExportJobRepository;
import br.com.joaomu.repository.UsuarioRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ExportJobService {

    private final ExportJobRepository jobRepository;
    private final UsuarioRepository usuarioRepository;
    private final DespesaRepository despesaRepository;
    private final RabbitTemplate rabbitTemplate;
    // ObjectMapper converte o objeto DespesaFiltroRequest em JSON string
    // para armazenar no campo "filtros" do ExportJob
    private final ObjectMapper objectMapper;

    public record ExportacaoSincronaResult(byte[] conteudo, long totalRegistros, long tempoProcessamentoMs) {}

    public ExportJobService(ExportJobRepository jobRepository,
            UsuarioRepository usuarioRepository,
            DespesaRepository despesaRepository,
            RabbitTemplate rabbitTemplate) {
        this.jobRepository = jobRepository;
        this.usuarioRepository = usuarioRepository;
        this.despesaRepository = despesaRepository;
        this.rabbitTemplate = rabbitTemplate;
        // Registra o módulo JavaTime para suportar LocalDate/LocalDateTime na
        // serialização
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ================================================================
    // Solicitar uma exportação — o método que retorna 202 Accepted
    // ================================================================

    @Transactional
    public ExportJob solicitarExportacao(DespesaFiltroRequest filtros, TipoExportacao tipo) {
        Usuario solicitante = getUsuarioLogado();

        // 1. Serializa os filtros como JSON para persistir no banco
        String filtrosJson;
        try {
            filtrosJson = objectMapper.writeValueAsString(filtros);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Erro ao serializar filtros: " + e.getMessage());
        }

        // 2. Cria o registro do job no banco com status ESPERANDO
        ExportJob job = new ExportJob();
        job.setId(UUID.randomUUID().toString()); // UUID gerado no Java, não no banco
        job.setStatus(StatusJob.ESPERANDO);
        job.setTipoExportacao(tipo);
        job.setFiltros(filtrosJson);
        job.setUsuarioSolicitante(solicitante);

        ExportJob jobSalvo = jobRepository.save(job);

        // 3. Publica mensagem na fila, o Worker processa de forma assíncrona
        // A mensagem é minimalista: só o ID do job
        // O Worker busca os filtros completos no banco pelo ID
        ExportJobEvent evento = new ExportJobEvent(
                jobSalvo.getId(),
                solicitante.getEmail(),
                solicitante.getNome() != null ? solicitante.getNome() : solicitante.getUsername());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_BATCHOUT,
                RabbitMQConfig.ROUTING_KEY_EXPORT,
                evento);

        // 4. Retorna o job imediatamente, o controller responde 202 Accepted
        // O frontend usa o job.getId() para fazer polling no status
        return jobSalvo;
    }

    // ================================================================
    // Consultar status, usado pelo endpoint de polling do frontend
    // ================================================================

    public ExportJob buscarPorId(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job não encontrado: " + jobId));
    }

    public List<ExportJob> listarMeusJobs() {
        Usuario usuario = getUsuarioLogado();
        return jobRepository.findByUsuarioSolicitanteIdOrderByDataSolicitacaoDesc(usuario.getId());
    }

    // Usado pelo Worker para atualizar o status durante o processamento
    @Transactional
    public ExportJob atualizarStatus(String jobId, StatusJob novoStatus) {
        ExportJob job = buscarPorId(jobId);
        job.setStatus(novoStatus);
        return jobRepository.save(job);
    }

    // ================================================================
    // Método utilitário interno
    // ================================================================

    private Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado no banco."));
    }

    // ================================================================
    // Geração Síncrona Bloqueante (Para comparação empírica de performance)
    // ================================================================
    public ExportacaoSincronaResult exportarSincrono(DespesaFiltroRequest filtros) {
        long inicio = System.currentTimeMillis();

        Long usuarioId = filtros != null ? filtros.usuarioId() : null;
        CategoriaDespesa categoria = filtros != null ? filtros.categoria() : null;
        StatusDespesa status = filtros != null ? filtros.status() : null;
        LocalDate dataInicio = filtros != null ? filtros.dataInicio() : null;
        LocalDate dataFim = filtros != null ? filtros.dataFim() : null;

        // Busca TODAS as despesas na memória de uma só vez (modo legado/sem paginação)
        List<Despesa> despesas = despesaRepository.buscarComFiltros(
                usuarioId, categoria, status, dataInicio, dataFim, Pageable.unpaged()
        ).getContent();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writer.write('\uFEFF'); // BOM UTF-8
            writer.write("ID,Titulo,Valor,Categoria,Status,Data Ocorrencia,Colaborador,Aprovador\n");

            for (Despesa despesa : despesas) {
                writer.write(formatarLinhaCsv(despesa));
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar CSV síncrono: " + e.getMessage(), e);
        }

        long tempoMs = System.currentTimeMillis() - inicio;
        return new ExportacaoSincronaResult(outputStream.toByteArray(), despesas.size(), tempoMs);
    }

    private String formatarLinhaCsv(Despesa despesa) {
        return String.join(",",
                String.valueOf(despesa.getId()),
                escapeCSV(despesa.getTitulo()),
                despesa.getValor().toPlainString(),
                despesa.getCategoria().name(),
                despesa.getStatus().name(),
                despesa.getDataOcorrencia().toString(),
                despesa.getUsuario() != null ? escapeCSV(despesa.getUsuario().getNome()) : "",
                despesa.getAprovador() != null ? escapeCSV(despesa.getAprovador().getNome()) : "") + "\n";
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
