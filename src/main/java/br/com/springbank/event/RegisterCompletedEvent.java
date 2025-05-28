package br.com.springbank.event;

import br.com.springbank.domain.entities.user.UserEntity;

public class RegisterCompletedEvent extends RegisterEvent {
    public RegisterCompletedEvent(UserEntity user) {
        super(user);
    }
}
