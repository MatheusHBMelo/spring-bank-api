package br.com.springbank.service.email;

import org.springframework.beans.factory.annotation.Value;
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
        var content = new SimpleMailMessage();

        content.setFrom(email);
        content.setTo(recipient);
        content.setSubject(title);
        content.setText(message);

        javaMailSender.send(content);
    }
}
