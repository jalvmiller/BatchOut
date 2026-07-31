package br.com.joaomu.dto;

// Mensagem publicada na fila batchout.export.queue pelo
// ExportJobService
// O Worker (ExportJobListener) desserializa esse record para saber
// qual job processar e com quais filtros

// A mensagem consiste em: jobId,emailSolicitante,nomeSolicitante

// Record é imutável e serializado automaticamente para JSON pelo Jackson
public record ExportJobEvent(
		// ID do ExportJob no banco, o Worker usa para
		// buscar os filtros e atualizar o status
		String jobId,

		// Email do solicitante, para notificar ao concluir
		String emailSolicitante,

		// Nome do solicitante, para personalizar o e-mail
		String nomeSolicitante) {
}
