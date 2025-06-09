package br.com.springbank.domain.repositories.user;

import br.com.springbank.domain.entities.user.RoleEntity;
import br.com.springbank.domain.entities.user.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Busca role com sucesso")
    void deveRetornarUmsRoleDadoSeuNome() {
        RoleEntity roleName = this.createRole(RoleEnum.USER);

        Optional<RoleEntity> createdRole = this.roleRepository.findByName(roleName.getName());

        assertThat(createdRole.isPresent()).isTrue();

        assertThat(createdRole.get().getId()).isEqualTo(roleName.getId());
        assertThat(createdRole.get().getName()).isEqualTo(roleName.getName());
    }

    @Test
    @DisplayName("Busca role sem sucesso")
    void deveRetornarVazioSeRoleNaoExistir() {
        Optional<RoleEntity> createdRole = this.roleRepository.findByName(RoleEnum.ADMIN);

        assertThat(createdRole.isEmpty()).isTrue();
    }

    private RoleEntity createRole(RoleEnum roleName) {
        RoleEntity newRole = RoleEntity.builder().name(roleName).permissions(Set.of()).build();

        this.roleRepository.save(newRole);

        return newRole;
    }
}