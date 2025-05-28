package br.com.springbank.listener;

import br.com.springbank.event.DepositCompletedEvent;
import br.com.springbank.event.TransferCompletedEvent;
import br.com.springbank.event.WithdrawCompletedEvent;
import br.com.springbank.service.email.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionNotificationListener {
    private final EmailService emailService;

    public TransactionNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void handleTransferCompleted(TransferCompletedEvent event) {
        String message = String.format(
                "Transferência de R$%.2f para a conta %s realizada com sucesso.",
                event.getAmount(),
                event.getReceiverAccountNumber()
        );
        emailService.sendEmail(event.getUser().getEmail(), "Transferência concluída", message);
    }

    @EventListener
    public void handleDepositCompleted(DepositCompletedEvent event) {
        String message = String.format("Depósito de R$%.2f realizado com sucesso.", event.getAmount());
        emailService.sendEmail(event.getUser().getEmail(), "Depósito concluído", message);
    }

    @EventListener
    public void handleWithdrawCompleted(WithdrawCompletedEvent event) {
        String message = String.format("Saque de R$%.2f realizado com sucesso.", event.getAmount());
        emailService.sendEmail(event.getUser().getEmail(), "Saque concluído", message);
    }
}
