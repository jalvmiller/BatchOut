package br.com.joaomu.controller;

import br.com.joaomu.dto.DespesaFiltroRequest;
import br.com.joaomu.entity.ExportJob;
import br.com.joaomu.entity.enums.TipoExportacao;
import br.com.joaomu.service.ExportJobService;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/export-jobs")
public class ExportJobController {

    private final ExportJobService exportJobService;

    public ExportJobController(ExportJobService exportJobService) {
        this.exportJobService = exportJobService;
    }

    // ================================================================
    // POST /export-jobs?tipo=CSV
    //
    // Exportação Assíncrona (BatchOut) — Retorna 202 Accepted em < 20ms
    // ================================================================
    @PostMapping
    public ResponseEntity<?> solicitarExportacao(
            @RequestBody DespesaFiltroRequest filtros,
            @RequestParam(defaultValue = "CSV") TipoExportacao tipo) {
        long inicio = System.currentTimeMillis();
        try {
            ExportJob job = exportJobService.solicitarExportacao(filtros, tipo);
            long tempoMs = System.currentTimeMillis() - inicio;

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Response-Time-MS", String.valueOf(tempoMs));

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .headers(headers)
                    .body(Map.of(
                            "jobId", job.getId(),
                            "status", job.getStatus(),
                            "tempoRespostaMs", tempoMs,
                            "mensagem", "Exportação assíncrona solicitada. Use o jobId para consultar o status."
                    ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", e.getMessage()));
        }
    }

    // ================================================================
    // POST /export-jobs/sincrono
    //
    // Exportação Síncrona Bloqueante (Para comparação empírica de performance)
    // Busca tudo no banco e constrói o CSV de forma síncrona no mesmo request.
    // ================================================================
    @PostMapping("/sincrono")
    public ResponseEntity<?> exportarSincrono(@RequestBody(required = false) DespesaFiltroRequest filtros) {
        try {
            DespesaFiltroRequest reqFiltros = filtros != null ? filtros : new DespesaFiltroRequest(null, null, null, null, null);
            ExportJobService.ExportacaoSincronaResult result = exportJobService.exportarSincrono(reqFiltros);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
            headers.setContentDisposition(ContentDisposition.attachment().filename("relatorio-despesas-sincrono.csv").build());
            headers.add("X-Response-Time-MS", String.valueOf(result.tempoProcessamentoMs()));
            headers.add("X-Total-Registros", String.valueOf(result.totalRegistros()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(result.conteudo());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", e.getMessage()));
        }
    }

    // ================================================================
    // GET /export-jobs/{jobId}/status
    //
    // Polling de status — o frontend chama a cada 3-5 segundos.
    // Quando status = CONCLUIDO, retorna também a arquivoUrl para download.
    // ================================================================
    @GetMapping("/{jobId}/status")
    public ResponseEntity<?> consultarStatus(@PathVariable String jobId) {
        try {
            ExportJob job = exportJobService.buscarPorId(jobId);
            return ResponseEntity.ok(Map.of(
                    "jobId", job.getId(),
                    "status", job.getStatus(),
                    "tipoExportacao", job.getTipoExportacao(),
                    "dataSolicitacao", job.getDataSolicitacao(),
                    "dataConclusao", job.getDataConclusao() != null ? job.getDataConclusao() : "",
                    "totalRegistros", job.getTotalRegistros() != null ? job.getTotalRegistros() : 0,
                    "arquivoUrl", job.getArquivoUrl() != null ? job.getArquivoUrl() : "",
                    "mensagemErro", job.getMensagemErro() != null ? job.getMensagemErro() : ""
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    // ================================================================
    // GET /export-jobs/meus
    //
    // Histórico de exportações do usuário logado, ordenado do mais
    // recente para o mais antigo.
    // ================================================================
    @GetMapping("/meus")
    public ResponseEntity<List<ExportJob>> meusJobs() {
        return ResponseEntity.ok(exportJobService.listarMeusJobs());
    }
}
