package br.com.springbank.service.exceptions.user;

import java.io.Serial;

public class UsernameRequiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UsernameRequiredException(String message) {
        super(message);
    }
}
