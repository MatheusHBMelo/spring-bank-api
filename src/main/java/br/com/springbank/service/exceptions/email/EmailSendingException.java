package br.com.springbank.service.exceptions.email;

import java.io.Serial;

public class EmailSendingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EmailSendingException(String message) {
        super(message);
    }
}
