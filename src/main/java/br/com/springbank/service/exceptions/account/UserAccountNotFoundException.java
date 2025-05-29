package br.com.springbank.service.exceptions.account;

import java.io.Serial;

public class UserAccountNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserAccountNotFoundException(String message) {
        super(message);
    }
}
