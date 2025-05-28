package br.com.springbank.domain.repositories.account;

import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    Optional<AccountEntity> findByUserEntity(UserEntity userEntity);

    boolean existsByAccountNumber(String accountNumber);
}
