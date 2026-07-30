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

    public void enviarEmailResolucao(String email, String assunto, String mensagem) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject(assunto);
        emailMessage.setText(mensagem);

        mailSender.send(emailMessage);
    }
}
