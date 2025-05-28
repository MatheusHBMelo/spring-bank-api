package br.com.springbank.event;

import br.com.springbank.domain.entities.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RegisterEvent {
    private final UserEntity userEntity;

    protected RegisterEvent(UserEntity user) {
        this.userEntity = user;
    }
}
