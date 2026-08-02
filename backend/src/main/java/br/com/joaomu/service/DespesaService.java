package br.com.joaomu.service;

import br.com.joaomu.entity.Despesa;
import br.com.joaomu.entity.Usuario;
import br.com.joaomu.entity.enums.StatusDespesa;
import br.com.joaomu.repository.DespesaRepository;
import br.com.joaomu.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DespesaService implements CrudService<Despesa, Long> {

    private final DespesaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public DespesaService(DespesaRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    // ========================
    // Métodos do CrudService
    // ========================

    @Override
    public List<Despesa> listarTodos() {
        return repository.findAll();
    }

    @Override
    public Despesa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada com ID: " + id));
    }

    // Comentário para enfatizar a obrigatoriedade do uso
    // de transactional nesse projeto..
    @Override
    @Transactional
    public Despesa salvar(Despesa despesa) {
        return validarESalvar(despesa);
    }

    @Override
    @Transactional
    public Despesa atualizar(Long id, Despesa dadosNovos) {
        Despesa existente = buscarPorId(id);
        verificarAutoria(existente);

        // Só permite editar enquanto PENDENTE — após decisão do gestor, a despesa é
        // imutável
        if (existente.getStatus() != StatusDespesa.PENDENTE) {
            throw new IllegalStateException("Não é possível editar uma despesa já " + existente.getStatus());
        }

        existente.setTitulo(dadosNovos.getTitulo());
        existente.setDescricao(dadosNovos.getDescricao());
        existente.setValor(dadosNovos.getValor());
        existente.setCategoria(dadosNovos.getCategoria());
        existente.setDataOcorrencia(dadosNovos.getDataOcorrencia());
        existente.setComprovanteUrl(dadosNovos.getComprovanteUrl());

        return repository.save(existente);
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Despesa despesa = buscarPorId(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Admin pode remover qualquer despesa; colaborador só remove a própria
        if (!isAdmin) {
            verificarAutoria(despesa);
        }

        repository.delete(despesa);
    }

    @Override
    public List<Despesa> buscarPorTermo(String termo) {
        if (termo == null || termo.isBlank()) {
            return repository.findAll();
        }
        return repository.search(termo);
    }

    // ================================================================
    // Regras de negócio específicas do domínio
    // ================================================================

    @Transactional
    public Despesa aprovar(Long id) {
        Despesa despesa = buscarPorId(id);

        if (despesa.getStatus() != StatusDespesa.PENDENTE) {
            throw new IllegalStateException("Apenas despesas PENDENTES podem ser aprovadas.");
        }

        Usuario gestor = getUsuarioLogado();
        despesa.setStatus(StatusDespesa.APROVADA);
        despesa.setAprovador(gestor);
        despesa.setDataAprovacao(LocalDateTime.now());

        return repository.save(despesa);
    }

    @Transactional
    public Despesa rejeitar(Long id, String motivo) {
        Despesa despesa = buscarPorId(id);

        if (despesa.getStatus() != StatusDespesa.PENDENTE) {
            throw new IllegalStateException("Apenas despesas PENDENTES podem ser rejeitadas.");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("O motivo da rejeição é obrigatório.");
        }

        Usuario gestor = getUsuarioLogado();
        despesa.setStatus(StatusDespesa.REJEITADA);
        despesa.setAprovador(gestor);
        despesa.setDataAprovacao(LocalDateTime.now());
        despesa.setMotivoRejeicao(motivo);

        return repository.save(despesa);
    }

    public List<Despesa> buscarMinhasDespesas() {
        Usuario usuario = getUsuarioLogado();
        return repository.findByUsuarioId(usuario.getId());
    }

    // Expõe o repository para o Worker de exportação (busca paginada em chunks)
    public DespesaRepository getRepository() {
        return repository;
    }

    // ================================================================
    // Métodos internos / utilitários
    // ================================================================

    @Transactional
    private Despesa validarESalvar(Despesa despesa) {
        if (despesa.getTitulo() == null || despesa.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título da despesa é obrigatório.");
        }
        if (despesa.getValor() == null || despesa.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero.");
        }
        if (despesa.getCategoria() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória.");
        }
        if (despesa.getDataOcorrencia() == null) {
            throw new IllegalArgumentException("Data de ocorrência é obrigatória.");
        }

        // Atribui o usuário logado automaticamente como dono da despesa
        if (despesa.getUsuario() == null) {
            despesa.setUsuario(getUsuarioLogado());
        }

        // Toda despesa nova começa como PENDENTE, independente do que vier no body
        despesa.setStatus(StatusDespesa.PENDENTE);

        return repository.save(despesa);
    }

    // Verifica se o usuário logado é o dono da despesa
    private void verificarAutoria(Despesa despesa) {
        Usuario logado = getUsuarioLogado();
        if (despesa.getUsuario() == null || !despesa.getUsuario().getId().equals(logado.getId())) {
            throw new SecurityException("Você não tem permissão para modificar esta despesa.");
        }
    }

    // Recupera o usuário logado via SecurityContextHolder + banco (com fallback para admin se anônimo)
    private Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return usuarioRepository.findByUsername(auth.getName())
                    .orElseGet(this::getUsuarioFallback);
        }
        return getUsuarioFallback();
    }

    private Usuario getUsuarioFallback() {
        return usuarioRepository.findByUsername("admin")
                .or(() -> usuarioRepository.findAll().stream().findFirst())
                .orElseThrow(() -> new IllegalStateException("Nenhum usuário cadastrado no sistema."));
    }
}
