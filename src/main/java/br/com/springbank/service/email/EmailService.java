package br.com.springbank.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${spring.app.email}")
    private String email;

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String recipient, String title, String message) {
        this.validateFields(recipient, title, message);

        var content = new SimpleMailMessage();

        content.setFrom(email);
        content.setTo(recipient);
        content.setSubject(title);
        content.setText(message);

        try {
            javaMailSender.send(content);
        } catch (MailException e) {
            throw new RuntimeException("Falha ao enviar e-mail.");
        }
    }

    private void validateFields(String recipient, String title, String message) {
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new IllegalArgumentException("Destinatário não pode ser nulo ou vazio.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Título do e-mail não pode ser nulo ou vazio.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem do e-mail não pode ser nula ou vazia.");
        }
    }
}
