package br.com.springbank.service.exceptions.user;

import java.io.Serial;

public class RoleNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RoleNotFoundException(String message) {
        super(message);
    }
}
