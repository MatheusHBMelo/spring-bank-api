package br.com.springbank.service.exceptions.user;

import java.io.Serial;

public class InactiveUserException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InactiveUserException(String message) {
        super(message);
    }
}
