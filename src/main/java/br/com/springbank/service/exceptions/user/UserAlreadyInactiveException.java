package br.com.springbank.service.exceptions.user;

import java.io.Serial;

public class UserAlreadyInactiveException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserAlreadyInactiveException(String message) {
        super(message);
    }
}
