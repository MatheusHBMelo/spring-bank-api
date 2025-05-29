package br.com.springbank.service.exceptions.token;

import java.io.Serial;

public class InvalidOrExpiredTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidOrExpiredTokenException(String message) {
        super(message);
    }
}
