package br.com.springbank.service.exceptions.token;

import java.io.Serial;

public class InvalidTokenSubjectException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTokenSubjectException(String message) {
        super(message);
    }
}
