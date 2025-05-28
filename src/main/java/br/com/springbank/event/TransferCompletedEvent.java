package br.com.springbank.event;

import br.com.springbank.domain.entities.user.UserEntity;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransferCompletedEvent extends TransactionEvent {
    private final String receiverAccountNumber;

    public TransferCompletedEvent(UserEntity user, BigDecimal amount, String receiverAccountNumber) {
        super(user, amount);
        this.receiverAccountNumber = receiverAccountNumber;
    }
}
