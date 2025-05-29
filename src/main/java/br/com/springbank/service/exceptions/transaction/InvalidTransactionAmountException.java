package br.com.springbank.service.exceptions.transaction;

import java.io.Serial;

public class InvalidTransactionAmountException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTransactionAmountException(String message) {
        super(message);
    }
}
