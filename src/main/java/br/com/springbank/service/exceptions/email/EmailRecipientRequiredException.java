package br.com.springbank.service.exceptions.email;

import java.io.Serial;

public class EmailRecipientRequiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EmailRecipientRequiredException(String message) {
        super(message);
    }
}
