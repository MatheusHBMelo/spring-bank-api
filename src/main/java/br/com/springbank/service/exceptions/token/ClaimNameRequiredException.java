package br.com.springbank.service.exceptions.token;

import java.io.Serial;

public class ClaimNameRequiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ClaimNameRequiredException(String message) {
        super(message);
    }
}
