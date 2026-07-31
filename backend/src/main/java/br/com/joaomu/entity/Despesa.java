package br.com.joaomu.entity;

import br.com.joaomu.entity.enums.CategoriaDespesa;
import br.com.joaomu.entity.enums.StatusDespesa;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

    // Aguardando aprovação do gestor
    // PENDENTE,
    // Aprovada pelo gestor
    // APROVADA,
    // Rejeitada pelo gestor
    // REJEITADA    

@Entity
@Table(name = "despesas")
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    // Coluna TEXT para descrições longas de justificativa
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // BigDecimal é o tipo correto para valores monetários (evita erros de ponto
    // flutuante)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // @Enumerated(STRING) persiste o nome textual do enum (ex: "TRANSPORTE"),
    // evitando problemas se a ordem dos valores mudar no futuro
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaDespesa categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusDespesa status = StatusDespesa.PENDENTE;

    // Data em que a despesa ocorreu de fato (diferente da data de cadastro)
    @Column(name = "data_ocorrencia", nullable = false)
    private LocalDate dataOcorrencia;

    // Data automática do cadastro no sistema
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // URL do comprovante (nota fiscal, recibo) armazenado no MinIO
    @Column(name = "comprovante_url", length = 500)
    private String comprovanteUrl;

    // Usuário que registrou a despesa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Gestor que aprovou ou rejeitou (pode ser nulo enquanto PENDENTE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovador_id")
    private Usuario aprovador;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    // Justificativa do gestor ao rejeitar
    @Column(name = "motivo_rejeicao", columnDefinition = "TEXT")
    private String motivoRejeicao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusDespesa.PENDENTE;
        }
    }

    // ===================== Getters e Setters =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public CategoriaDespesa getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDespesa categoria) {
        this.categoria = categoria;
    }

    public StatusDespesa getStatus() {
        return status;
    }

    public void setStatus(StatusDespesa status) {
        this.status = status;
    }

    public LocalDate getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(LocalDate dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getComprovanteUrl() {
        return comprovanteUrl;
    }

    public void setComprovanteUrl(String comprovanteUrl) {
        this.comprovanteUrl = comprovanteUrl;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getAprovador() {
        return aprovador;
    }

    public void setAprovador(Usuario aprovador) {
        this.aprovador = aprovador;
    }

    public LocalDateTime getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(LocalDateTime dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
}
