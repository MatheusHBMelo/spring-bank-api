package br.com.springbank.listener;

import br.com.springbank.event.RegisterCompletedEvent;
import br.com.springbank.service.email.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class RegisterNotificationListener {
    private final EmailService emailService;

    public RegisterNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void handleRegisterCompleted(RegisterCompletedEvent event) {
        String message = String.format(
                "Confirmamos que sua conta foi criada com sucesso no sistema, Sr.%s",
                event.getUserEntity().getUsername()
        );

        this.emailService.sendEmail(event.getUserEntity().getEmail(), "Confirmação de conta", message);
    }
}
