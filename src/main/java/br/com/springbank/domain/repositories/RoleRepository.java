package br.com.springbank.domain.repositories;

import br.com.springbank.domain.entities.user.RoleEntity;
import br.com.springbank.domain.entities.user.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(RoleEnum name);
}
