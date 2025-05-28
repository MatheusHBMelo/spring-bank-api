package br.com.springbank.event;

import br.com.springbank.domain.entities.user.UserEntity;

import java.math.BigDecimal;

public class WithdrawCompletedEvent extends TransactionEvent {
    public WithdrawCompletedEvent(UserEntity user, BigDecimal amount) {
        super(user, amount);
    }
}
