package br.com.springbank.domain.repositories.account;

import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.account.TransactionEntity;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.domain.repositories.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Busca transacoes com sucesso")
    void deveRetornarTodasAsTransacoesDadoIdDaConta() {
        TransactionEntity transaction = this.createTransaction();
        Long accountId = transaction.getSourceAccount().getId();

        List<TransactionEntity> transactions = this.transactionRepository.findAllByAccountId(accountId);

        assertEquals(1, transactions.size());
    }

    @Test
    @DisplayName("Busca transacoes sem sucesso")
    void deveRetornarTransacoesVaziasDadoIdDaContaNaoExistir() {
        List<TransactionEntity> transactions = this.transactionRepository.findAllByAccountId(2L);

        assertThat(transactions.size()).isEqualTo(0);
    }

    private TransactionEntity createTransaction() {
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
                .accountNumber("12345678")
                .agencyNumber("001")
                .balance(new BigDecimal("100.00"))
                .userEntity(usercreated)
                .createdAt(LocalDateTime.now())
                .build();

        AccountEntity accountCreated = this.accountRepository.save(newAccount);

        TransactionEntity newTransactions = TransactionEntity.builder()
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("50.00"))
                .sourceAccount(accountCreated)
                .createdAt(LocalDateTime.now())
                .build();

        return this.transactionRepository.save(newTransactions);
    }
}