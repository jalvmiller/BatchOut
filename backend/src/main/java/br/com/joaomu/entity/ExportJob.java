package br.com.joaomu.entity;

import br.com.joaomu.entity.enums.StatusJob;
import br.com.joaomu.entity.enums.TipoExportacao;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// Job criado, aguardando processamento na fila
// ESPERANDO,
// Worker está processando os chunks
// PROCESSANDO,
// Arquivo gerado e disponível para download
// CONCLUIDO,
// Erro durante o processamento
// FALHOU

@Entity
@Table(name = "export_jobs")
public class ExportJob {

    // UUID como string, garante unicidade global sem depender do auto-increment
    // e é seguro para expor ao frontend como identificador de polling
    // 36 caracteres
    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusJob status = StatusJob.ESPERANDO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_exportacao", nullable = false, length = 10)
    private TipoExportacao tipoExportacao;

    // Filtros serializados como JSON
    // (ex: {"dataInicio":"2025-01",
    // "categoria":"TRANSPORTE"})
    // Armazenado como TEXT para flexibilidade

    // O Worker desserializa na hora de processar
    @Column(columnDefinition = "TEXT")
    private String filtros;

    // Usuário que solicitou o relatório
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuarioSolicitante;

    @Column(name = "data_solicitacao", nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    // URL pré-assinada do MinIO (expira em 15 minutos, gerada ao COMPLETED)
    @Column(name = "arquivo_url", length = 1000)
    private String arquivoUrl;

    // Nome do arquivo no bucket para regeração de URL se expirar
    @Column(name = "arquivo_key", length = 500)
    private String arquivoKey;

    // Quantidade de registros processados — útil para métricas no currículo
    @Column(name = "total_registros")
    private Long totalRegistros;

    // Mensagem de erro para diagnóstico quando status = FALHOU
    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;

    @PrePersist
    protected void onCreate() {
        this.dataSolicitacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusJob.ESPERANDO;
        }
    }

    // ===================== Getters e Setters =====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusJob getStatus() {
        return status;
    }

    public void setStatus(StatusJob status) {
        this.status = status;
    }

    public TipoExportacao getTipoExportacao() {
        return tipoExportacao;
    }

    public void setTipoExportacao(TipoExportacao tipoExportacao) {
        this.tipoExportacao = tipoExportacao;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Usuario getUsuarioSolicitante() {
        return usuarioSolicitante;
    }

    public void setUsuarioSolicitante(Usuario usuarioSolicitante) {
        this.usuarioSolicitante = usuarioSolicitante;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getArquivoUrl() {
        return arquivoUrl;
    }

    public void setArquivoUrl(String arquivoUrl) {
        this.arquivoUrl = arquivoUrl;
    }

    public String getArquivoKey() {
        return arquivoKey;
    }

    public void setArquivoKey(String arquivoKey) {
        this.arquivoKey = arquivoKey;
    }

    public Long getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Long totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }
}
