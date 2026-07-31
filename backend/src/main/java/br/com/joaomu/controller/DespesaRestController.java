package br.com.joaomu.controller;

import br.com.joaomu.entity.Despesa;
import br.com.joaomu.service.DespesaService;
import br.com.joaomu.service.UploadService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/despesas")
public class DespesaRestController extends BaseRestController<Despesa, Long> {

    private final DespesaService despesaService;
    private final UploadService uploadService;

    public DespesaRestController(DespesaService despesaService, UploadService uploadService) {
        super(despesaService);
        this.despesaService = despesaService;
        this.uploadService = uploadService;
    }

    // GET /despesas e GET /despesas?busca=... herdados do BaseRestController
    // POST /despesas herdado do BaseRestController
    // PUT /despesas/{id} herdado do BaseRestController
    // DELETE /despesas/{id} herdado do BaseRestController

    // ===================================================
    // Listagem das despesas do próprio usuário logado
    // ===================================================

    @GetMapping("/minhas")
    public ResponseEntity<List<Despesa>> minhasDespesas() {
        return ResponseEntity.ok(despesaService.buscarMinhasDespesas());
    }

    // ===================================================
    // Upload de comprovante (nota fiscal, recibo)
    // ===================================================

    @PostMapping("/{id}/comprovante")
    public ResponseEntity<?> uploadComprovante(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Despesa despesa = despesaService.buscarPorId(id);
            String url = uploadService.uploadImage(file);

            despesa.setComprovanteUrl(url);
            despesaService.getRepository().save(despesa);

            return ResponseEntity.ok(Map.of("comprovanteUrl", url));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", e.getMessage()));
        }
    }

    // ===================================================
    // Aprovação e Rejeição
    // Requer ROLE_SPECIALIST (gestor) ou ROLE_ADMIN
    // ===================================================

    // @PreAuthorize avalia a expressão ANTES de entrar no método.
    // Depende do @EnableMethodSecurity ativado no SecurityConfig.
    @PutMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('SPECIALIST', 'ADMIN')")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        try {

            Despesa aprovada = despesaService.aprovar(id);

            return ResponseEntity.ok(aprovada);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('SPECIALIST', 'ADMIN')")
    public ResponseEntity<?> rejeitar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {

            String motivo = body.get("motivo");
            Despesa rejeitada = despesaService.rejeitar(id, motivo);

            return ResponseEntity.ok(rejeitada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        }
    }
}
