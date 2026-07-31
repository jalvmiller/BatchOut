package br.com.joaomu.service;

import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class NotificacaoService {

    private final JavaMailSender mailSender;

    public NotificacaoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Notificação genérica — base para todos os outros métodos
    private void enviarEmail(String destinatario, String assunto, String corpo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject(assunto);
        mensagem.setText(corpo);
        mailSender.send(mensagem);
    }

    // Notificação de exportação concluída — chamada pelo ExportJobListener
    // quando o Worker termina de gerar o arquivo CSV/PDF
    public void notificarExportacaoConcluida(String email, String nome, String jobId, String downloadUrl) {
        String assunto = "BatchOut — Seu relatório está pronto para download";
        String corpo = String.format(
            "Olá, %s!\n\n" +
            "A exportação solicitada (ID: %s) foi concluída com sucesso.\n\n" +
            "Clique no link abaixo para baixar o arquivo:\n%s\n\n" +
            "Atenção: O link expira em 15 minutos por segurança.\n\n" +
            "Atenciosamente,\nEquipe BatchOut",
            nome, jobId, downloadUrl
        );
        enviarEmail(email, assunto, corpo);
    }

    // Notificação de falha na exportação
    public void notificarExportacaoFalhou(String email, String nome, String jobId) {
        String assunto = "BatchOut — Falha na geração do relatório";
        String corpo = String.format(
            "Olá, %s!\n\n" +
            "Infelizmente, a exportação solicitada (ID: %s) encontrou um erro durante o processamento.\n\n" +
            "Tente novamente ou entre em contato com o suporte.\n\n" +
            "Atenciosamente,\nEquipe BatchOut",
            nome, jobId
        );
        enviarEmail(email, assunto, corpo);
    }

    // Notificação de despesa aprovada — para o colaborador
    public void notificarDespesaAprovada(String email, String nome, String tituloDespesa) {
        String assunto = "BatchOut — Despesa aprovada";
        String corpo = String.format(
            "Olá, %s!\n\nSua despesa \"%s\" foi aprovada.\n\nAtenciosamente,\nEquipe BatchOut",
            nome, tituloDespesa
        );
        enviarEmail(email, assunto, corpo);
    }

    // Notificação de despesa rejeitada — inclui o motivo da rejeição
    public void notificarDespesaRejeitada(String email, String nome, String tituloDespesa, String motivo) {
        String assunto = "BatchOut — Despesa rejeitada";
        String corpo = String.format(
            "Olá, %s!\n\nSua despesa \"%s\" foi rejeitada.\n\nMotivo: %s\n\nAtenciosamente,\nEquipe BatchOut",
            nome, tituloDespesa, motivo
        );
        enviarEmail(email, assunto, corpo);
    }
}
