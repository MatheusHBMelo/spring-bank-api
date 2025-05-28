package br.com.springbank.event;

import br.com.springbank.domain.entities.user.UserEntity;

import java.math.BigDecimal;

public class DepositCompletedEvent extends TransactionEvent {
    public DepositCompletedEvent(UserEntity user, BigDecimal amount) {
        super(user, amount);
    }
}
