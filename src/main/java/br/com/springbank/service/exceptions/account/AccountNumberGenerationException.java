package br.com.springbank.service.exceptions.account;

import java.io.Serial;

public class AccountNumberGenerationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AccountNumberGenerationException(String message) {
        super(message);
    }
}
