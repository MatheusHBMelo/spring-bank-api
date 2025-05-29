package br.com.springbank.service.email;

import br.com.springbank.service.exceptions.email.EmailMessageRequiredException;
import br.com.springbank.service.exceptions.email.EmailRecipientRequiredException;
import br.com.springbank.service.exceptions.email.EmailSendingException;
import br.com.springbank.service.exceptions.email.EmailTitleRequiredException;
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
            throw new EmailSendingException("Falha ao enviar e-mail: " + e.getMessage());
        }
    }

    private void validateFields(String recipient, String title, String message) {
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new EmailRecipientRequiredException("Destinatário do e-mail não pode ser nulo ou vazio.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new EmailTitleRequiredException("Título do e-mail não pode ser nulo ou vazio.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new EmailMessageRequiredException("Mensagem do e-mail não pode ser nula ou vazia.");
        }
    }
}
