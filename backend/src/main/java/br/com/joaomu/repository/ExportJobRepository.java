package br.com.joaomu.repository;

import br.com.joaomu.entity.ExportJob;
import br.com.joaomu.entity.enums.StatusJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// PK é String (UUID)
public interface ExportJobRepository extends JpaRepository<ExportJob, String> {

    // Histórico de jobs do usuário (para listar na tela do painel)
    List<ExportJob> findByUsuarioSolicitanteIdOrderByDataSolicitacaoDesc(Long usuarioId);

    // Busca por status — útil para reprocessar jobs que travaram em PROCESSANDO
    List<ExportJob> findByStatus(StatusJob status);
}
