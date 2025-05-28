package br.com.springbank.validator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionValidator {
    public void validatePositiveAmount(BigDecimal amount, String operationName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor do " + operationName.toLowerCase() + " deve ser no mínimo R$0.01");
        }
    }

    public void validateSufficientBalance(BigDecimal balance, BigDecimal amount, String operationName) {
        if (balance.compareTo(amount) < 0) {
            throw new RuntimeException("Saldo insuficiente para " + operationName);
        }
    }
}
