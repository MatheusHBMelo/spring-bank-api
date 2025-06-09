package br.com.springbank.domain.repositories.account;

import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Busca account com sucesso")
    void deveRetornarAccountDadoSeuNumeroComSucesso() {
        AccountEntity account = AccountEntity.builder()
                .accountNumber("12345678")
                .agencyNumber("001")
                .balance(new BigDecimal("100.00"))
                .build();

        AccountEntity contacriada = this.createAccount(account);

        Optional<AccountEntity> createdAccount = this.accountRepository.findByAccountNumber("12345678");

        assertThat(createdAccount.isPresent()).isTrue();

        assertThat(createdAccount.get().getId()).isEqualTo(contacriada.getId());
        assertThat(createdAccount.get().getAccountNumber()).isEqualTo(contacriada.getAccountNumber());
        assertThat(createdAccount.get().getBalance()).isEqualTo(contacriada.getBalance());
        assertThat(createdAccount.get().getAgencyNumber()).isEqualTo(contacriada.getAgencyNumber());
    }

    @Test
    @DisplayName("Busca account sem sucesso")
    void deveRetornarAccountVaziaDadoSeuNumeroNaoExista() {
        Optional<AccountEntity> createdAccount = this.accountRepository.findByAccountNumber("12345678");

        assertThat(createdAccount.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Busca account por userEntity com sucesso")
    void deveRetornarAccountDadoUserEntityComSucesso() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("Matheus")
                .email("matheus@email.com")
                .password("12345")
                .role(Set.of())
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        AccountEntity account = AccountEntity.builder()
                .accountNumber("12345678")
                .agencyNumber("001")
                .balance(new BigDecimal("100.00"))
                .build();

        AccountEntity contacriada = this.createAccount(account);

        Optional<AccountEntity> createdAccount = this.accountRepository.findByUserEntity(user);

        assertThat(createdAccount.isPresent()).isTrue();

        assertThat(createdAccount.get().getId()).isEqualTo(contacriada.getId());
        assertThat(createdAccount.get().getAccountNumber()).isEqualTo(contacriada.getAccountNumber());
        assertThat(createdAccount.get().getBalance()).isEqualTo(contacriada.getBalance());
        assertThat(createdAccount.get().getAgencyNumber()).isEqualTo(contacriada.getAgencyNumber());
    }

    @Test
    @DisplayName("Busca account por userEntity sem sucesso")
    void deveRetornarAccountVaziaDadoUserEntityNaoExista() {
        Optional<AccountEntity> createdAccount = this.accountRepository.findByUserEntity(null);

        assertThat(createdAccount.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Retorna status true se existir conta")
    void deveRetornarTrueSeExistirAccountDadoUmAccountNumber() {
        AccountEntity account = AccountEntity.builder()
                .accountNumber("12345678")
                .agencyNumber("001")
                .balance(new BigDecimal("100.00"))
                .build();

        this.createAccount(account);

        boolean result = this.accountRepository.existsByAccountNumber("12345678");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Retorna status false se não existir conta")
    void deveRetornarFalseSeExistirAccountDadoUmAccountNumber() {
        boolean result = this.accountRepository.existsByAccountNumber("12345678");

        assertThat(result).isFalse();
    }

    private AccountEntity createAccount(AccountEntity account) {

        UserEntity user = UserEntity.builder()
                .username("Matheus")
                .email("matheus@email.com")
                .password("12345")
                .role(Set.of())
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity usercreated = this.userRepository.save(user);

        AccountEntity newAccount = AccountEntity.builder()
                .accountNumber(account.getAccountNumber())
                .agencyNumber(account.getAgencyNumber())
                .balance(account.getBalance())
                .userEntity(usercreated)
                .createdAt(LocalDateTime.now())
                .build();

        this.accountRepository.save(newAccount);

        return newAccount;
    }

}