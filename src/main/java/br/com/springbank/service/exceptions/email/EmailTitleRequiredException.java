package br.com.springbank.service.exceptions.email;

import java.io.Serial;

public class EmailTitleRequiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EmailTitleRequiredException(String message) {
        super(message);
    }
}
