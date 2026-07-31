package br.com.joaomu.repository;

import br.com.joaomu.entity.Despesa;
import br.com.joaomu.entity.enums.CategoriaDespesa;
import br.com.joaomu.entity.enums.StatusDespesa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

        // Busca todas as despesas de um usuário específico
        List<Despesa> findByUsuarioId(Long usuarioId);

        // Busca por categoria
        List<Despesa> findByCategoria(CategoriaDespesa categoria);

        // Busca por status (PENDENTE, APROVADA, REJEITADA)
        List<Despesa> findByStatus(StatusDespesa status);

        // Busca por usuário + status (ex: "minhas despesas pendentes")
        List<Despesa> findByUsuarioIdAndStatus(Long usuarioId, StatusDespesa status);

        // Busca por intervalo de datas, usado pelos filtros da exportação batch
        List<Despesa> findByDataOcorrenciaBetween(LocalDate inicio, LocalDate fim);

        // Busca paginada com múltiplos filtros opcionais.
        // É o método mais importante: o Worker de exportação o usa para buscar
        // em CHUNKS sem carregar tudo na memória (Pageable controla o tamanho do
        // chunk).
        // null em qualquer parâmetro = sem filtro aplicado para aquele campo
        @Query("""
                        SELECT d FROM Despesa d
                        WHERE (:usuarioId IS NULL OR d.usuario.id = :usuarioId)
                        AND (:categoria IS NULL OR d.categoria = :categoria)
                        AND (:status IS NULL OR d.status = :status)
                        AND (:dataInicio IS NULL OR d.dataOcorrencia >= :dataInicio)
                        AND (:dataFim IS NULL OR d.dataOcorrencia <= :dataFim)
                        ORDER BY d.dataOcorrencia DESC
                        """)
        Page<Despesa> buscarComFiltros(
                        @Param("usuarioId") Long usuarioId,
                        @Param("categoria") CategoriaDespesa categoria,
                        @Param("status") StatusDespesa status,
                        @Param("dataInicio") LocalDate dataInicio,
                        @Param("dataFim") LocalDate dataFim,
                        Pageable pageable);

        // Busca por termo no título ou descrição
        // (para o endpoint ?busca= do BaseRestController)
        // Uniformiza a busca para letra minúscula, etc
        @Query("SELECT d FROM Despesa d WHERE"
                        + " LOWER(d.titulo) LIKE LOWER(CONCAT('%', :termo, '%'))"
                        + " OR LOWER(d.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))")
        List<Despesa> search(@Param("termo") String termo);

        // Contagem por status útil para dashboard
        long countByStatus(StatusDespesa status);
}
