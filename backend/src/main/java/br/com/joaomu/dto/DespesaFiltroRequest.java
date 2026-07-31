package br.com.joaomu.dto;

import br.com.joaomu.entity.enums.CategoriaDespesa;
import br.com.joaomu.entity.enums.StatusDespesa;

import java.time.LocalDate;

// DTO usado como filtro na exportação batch e também nas buscas da listagem
// Record é imutável por natureza, ideal para objetos de transferência de dados
// Todos os campos são opcionais (null = sem filtro aplicado)
public record DespesaFiltroRequest(
                Long usuarioId,
                CategoriaDespesa categoria,
                StatusDespesa status,
                LocalDate dataInicio,
                LocalDate dataFim) {
}
