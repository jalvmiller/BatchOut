package br.com.joaomu.controller;

import br.com.joaomu.dto.DespesaFiltroRequest;
import br.com.joaomu.entity.ExportJob;
import br.com.joaomu.entity.enums.TipoExportacao;
import br.com.joaomu.service.ExportJobService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    // Solicita uma exportação. Retorna 202 Accepted imediatamente com
    // o jobId para que o frontend faça polling no status.
    //
    // Fluxo:
    // 1. Cria ExportJob no banco com status ESPERANDO
    // 2. Publica mensagem na fila do RabbitMQ
    // 3. Responde em < 20ms com { jobId, status: "ESPERANDO" }
    // ================================================================
    @PostMapping
    public ResponseEntity<?> solicitarExportacao(
            @RequestBody DespesaFiltroRequest filtros,
            @RequestParam(defaultValue = "CSV") TipoExportacao tipo) {
        try {
            ExportJob job = exportJobService.solicitarExportacao(filtros, tipo);
            // 202 Accepted = "recebemos, mas ainda não processamos"
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "jobId", job.getId(),
                    "status", job.getStatus(),
                    "mensagem", "Exportação solicitada. Use o jobId para consultar o status."
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", e.getMessage()));
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
