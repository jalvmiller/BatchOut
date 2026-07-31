package br.com.joaomu.entity.enums;

public enum StatusJob {
    // Job criado, aguardando processamento na fila
    ESPERANDO,
    // Worker está processando os chunks
    PROCESSANDO,
    // Arquivo gerado e disponível para download
    CONCLUIDO,
    // Erro durante o processamento
    FALHOU
}
