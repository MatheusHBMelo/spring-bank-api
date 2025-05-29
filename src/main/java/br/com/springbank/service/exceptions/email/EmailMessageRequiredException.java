package br.com.springbank.service.exceptions.email;

import java.io.Serial;

public class EmailMessageRequiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EmailMessageRequiredException(String message) {
        super(message);
    }
}
