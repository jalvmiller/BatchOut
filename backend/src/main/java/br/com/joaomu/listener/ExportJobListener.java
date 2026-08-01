package br.com.joaomu.listener;

import br.com.joaomu.config.RabbitMQConfig;
import br.com.joaomu.dto.DespesaFiltroRequest;
import br.com.joaomu.dto.ExportJobEvent;
import br.com.joaomu.entity.Despesa;
import br.com.joaomu.entity.ExportJob;
import br.com.joaomu.entity.enums.CategoriaDespesa;
import br.com.joaomu.entity.enums.StatusDespesa;
import br.com.joaomu.entity.enums.StatusJob;
import br.com.joaomu.repository.DespesaRepository;
import br.com.joaomu.repository.ExportJobRepository;
import br.com.joaomu.service.ExportJobService;
import br.com.joaomu.service.NotificacaoService;
import br.com.joaomu.service.UploadService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class ExportJobListener {

    // Tamanho de cada chunk (página) — processa 500 despesas por vez na memória
    // Em vez de carregar 50.000 registros de uma vez, carrega 500, escreve no CSV,
    // descarta da memória e carrega os próximos 500. Isso evita OutOfMemoryError.
    private static final int CHUNK_SIZE = 500;

    private final ExportJobRepository jobRepository;
    private final DespesaRepository despesaRepository;
    private final ExportJobService exportJobService;
    private final NotificacaoService notificacaoService;
    private final UploadService uploadService;
    private final ObjectMapper objectMapper;

    public ExportJobListener(ExportJobRepository jobRepository,
            DespesaRepository despesaRepository,
            ExportJobService exportJobService,
            NotificacaoService notificacaoService,
            UploadService uploadService) {
        this.jobRepository = jobRepository;
        this.despesaRepository = despesaRepository;
        this.exportJobService = exportJobService;
        this.notificacaoService = notificacaoService;
        this.uploadService = uploadService;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ================================================================
    // O Worker — consome mensagens da fila de exportação
    //
    // @RabbitListener registra este método como consumidor da fila.
    // O Spring AMQP o chama automaticamente quando uma mensagem chega.
    // Se lançar exceção, a mensagem vai para reprocessamento (retry).
    // ================================================================
    @RabbitListener(queues = RabbitMQConfig.QUEUE_EXPORT)
    public void processarExportacao(ExportJobEvent evento) {
        System.out.println("====== WORKER: Job recebido ======");
        System.out.println("JobId: " + evento.jobId());

        ExportJob job = null;
        try {
            // 1. Busca o job no banco e atualiza para PROCESSANDO
            job = exportJobService.buscarPorId(evento.jobId());
            job.setStatus(StatusJob.PROCESSANDO);
            jobRepository.save(job);

            // 2. Desserializa os filtros do campo JSON do banco
            DespesaFiltroRequest filtros = objectMapper.readValue(job.getFiltros(), DespesaFiltroRequest.class);

            // 3. Gera o CSV em chunks (paginação)
            String arquivoKey = gerarCsvEmChunks(job, filtros);

            // 4. Gera a Pre-signed URL do MinIO (expira em 15 minutos)
            String preSignedUrl = uploadService.gerarPreSignedUrl(arquivoKey, 15);

            // 5. Atualiza o job para CONCLUIDO com a URL de download
            job.setStatus(StatusJob.CONCLUIDO);
            job.setArquivoKey(arquivoKey);
            job.setArquivoUrl(preSignedUrl);
            job.setDataConclusao(LocalDateTime.now());
            jobRepository.save(job);

            System.out
                    .println("Job " + evento.jobId() + " concluído. Total: " + job.getTotalRegistros() + " registros.");

            // Notifica o solicitante por e-mail com o link de download
            if (evento.emailSolicitante() != null) {
                notificacaoService.notificarExportacaoConcluida(
                        evento.emailSolicitante(),
                        evento.nomeSolicitante(),
                        job.getId(),
                        preSignedUrl);
            }

        } catch (Exception e) {
            // Se qualquer erro ocorrer, marca o job como FALHOU e registra a mensagem
            System.err.println("Erro ao processar job " + evento.jobId() + ": " + e.getMessage());
            if (job != null) {
                job.setStatus(StatusJob.FALHOU);
                job.setMensagemErro(e.getMessage());
                job.setDataConclusao(LocalDateTime.now());
                jobRepository.save(job);

                // Notifica o solicitante que a exportação falhou
                if (evento.emailSolicitante() != null) {
                    try {
                        notificacaoService.notificarExportacaoFalhou(
                                evento.emailSolicitante(),
                                evento.nomeSolicitante(),
                                job.getId());
                    } catch (Exception emailEx) {
                        System.err.println("Falha ao enviar e-mail de erro: " + emailEx.getMessage());
                    }
                }
            }
            // Não relança a exceção, o job foi marcado como FALHOU,
            // não pode voltar para a fila
        }
    }

    // ================================================================
    // Geração do CSV em chunks
    //
    // Em vez de SELECT * FROM despesas (pode trazer 100.000 linhas),
    // SELECT ... LIMIT 500 OFFSET 0, depois OFFSET 500, etc.
    // O OFFSET é um deslocamento apenas, para progredir em cada iteração
    // Cada página é processada e descartada antes de carregar a próxima
    // ================================================================
    private String gerarCsvEmChunks(ExportJob job, DespesaFiltroRequest filtros) throws Exception {
        // O outputStream será onde o CSV será construído
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // O writer é usado para escrever no outputStream
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        // Cabeçalho do CSV com BOM UTF-8 para Excel abrir corretamente
        // O BOM (﻿) é um caractere especial que indica que o arquivo está em UTF-8.
        // Garante que o Excel reconheça o arquivo como UTF-8 e não mostre caracteres
        // estranhos no lugar de acentos
        // E ele é escrito com "\uFEFF"
        writer.write('\uFEFF');
        // Cabeçalho do CSV com as colunas que serão exportadas
        writer.write("ID,Titulo,Valor,Categoria,Status,Data Ocorrencia,Colaborador,Aprovador\n");
        // ================== Parte do Excel com a geração do CSV ==================

        // ================== Parte do loop das páginas ============================
        long totalRegistros = 0;
        int pagina = 0;
        // Page é uma interface do Spring Data que representa uma página de dados.
        // Contém a lista de elementos ( getContent() )
        // O número da página ( getNumber() )
        // O tamanho da página ( getSize() )
        // O total de elementos ( getTotalElements() )
        // O número total de páginas ( getTotalPages() )
        // Se tem próxima página ( hasNext() )
        // Se tem página anterior ( hasPrevious() )
        Page<Despesa> chunk;

        // Monta os parâmetros de filtro (null = sem filtro para aquele campo)
        Long usuarioId = filtros.usuarioId();
        CategoriaDespesa categoria = filtros.categoria();
        StatusDespesa status = filtros.status();

        // Loop: processa uma página por vez até não ter mais dados
        do {
            chunk = despesaRepository.buscarComFiltros(
                    usuarioId,
                    categoria,
                    status,
                    filtros.dataInicio(),
                    filtros.dataFim(),
                    PageRequest.of(pagina, CHUNK_SIZE));

            // Escreve cada linha do chunk no CSV
            for (Despesa despesa : chunk.getContent()) {
                writer.write(formatarLinhaCsv(despesa));
                totalRegistros++;
            }

            pagina++;
        } while (chunk.hasNext()); // continua enquanto houver próxima página

        writer.flush(); // força a gravação do conteúdo do buffer na saída

        // ========== Parte final, upload e notificação =========================
        // Atualiza a contagem de registros no job
        job.setTotalRegistros(totalRegistros);
        jobRepository.save(job);

        // Faz upload do CSV para o MinIO e retorna o nome do arquivo no bucket
        String nomeArquivo = "exports/relatorio-" + job.getId() + ".csv";
        uploadService.uploadBytes(
                new ByteArrayInputStream(outputStream.toByteArray()),
                nomeArquivo,
                "text/csv");

        return nomeArquivo;
    }

    // Usado no método principal
    // Formata uma linha de despesa no padrão CSV
    // escapeCSV evita quebra de campos que contêm vírgulas ou aspas
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

    // Usado no método formatarLinhaCsv
    // Envolve o valor em aspas duplas se ele contiver
    // vírgula, aspas ou quebras de linha
    private String escapeCSV(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
