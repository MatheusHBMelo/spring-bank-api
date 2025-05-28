package br.com.springbank.event;

import br.com.springbank.domain.entities.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public abstract class TransactionEvent {
    private final UserEntity user;
    private final BigDecimal amount;

    protected TransactionEvent(UserEntity user, BigDecimal amount) {
        this.user = user;
        this.amount = amount;
    }
}
