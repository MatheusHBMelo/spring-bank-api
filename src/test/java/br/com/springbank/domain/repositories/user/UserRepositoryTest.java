package br.com.springbank.domain.repositories.user;

import br.com.springbank.controller.auth.dto.RegisterDto;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Busca usuario com sucesso")
    void deveRetornarUmUsuarioDadoSeuUsernameComSucesso() {
        RegisterDto dto = new RegisterDto("Matheus", "12345", "matheus@email.com");

        UserEntity userCriado = this.createUser(dto);

        Optional<UserEntity> createdUser = this.userRepository.findByUsername("Matheus");

        assertThat(createdUser.isPresent()).isTrue();

        assertThat(createdUser.get().getId()).isEqualTo(userCriado.getId());
        assertThat(createdUser.get().getUsername()).isEqualTo(userCriado.getUsername());
        assertThat(createdUser.get().getPassword()).isEqualTo(userCriado.getPassword());
        assertThat(createdUser.get().getEmail()).isEqualTo(userCriado.getEmail());
        assertThat(createdUser.get().getStatus()).isEqualTo(userCriado.getStatus());
    }

    @Test
    @DisplayName("Busca usuario sem sucesso")
    void deveRetornarVazioCasoNaoEncontraUsuario() {
        Optional<UserEntity> createdUser = this.userRepository.findByUsername("Matheus");

        assertThat(createdUser.isEmpty()).isTrue();
    }

    private UserEntity createUser(RegisterDto registerDto) {
        UserEntity newUser = UserEntity.builder().username(registerDto.username()).email(registerDto.email())
                .password(registerDto.password()).role(Set.of()).status(StatusEnum.ACTIVE).createdAt(LocalDateTime.now()).build();

        this.userRepository.save(newUser);

        return newUser;
    }
}