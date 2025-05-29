package br.com.springbank.service.exceptions.transaction;

import java.io.Serial;

public class InsufficientBalanceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
