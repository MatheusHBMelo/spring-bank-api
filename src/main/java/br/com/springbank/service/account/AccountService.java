package br.com.springbank.service.account;

import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.utils.AccountNumberGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepository accountRepository, AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    public AccountEntity getUserAccount(UserEntity user) {
        return accountRepository.findByUserEntity(user)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));
    }

    public AccountEntity getUserAccountByAccountNumber(String accountNumber) {
        return this.accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Não existe conta com esse número."));
    }

    public void subtractBalance(AccountEntity userAccount, BigDecimal amount) {
        userAccount.setBalance(userAccount.getBalance().subtract(amount));

        this.accountRepository.save(userAccount);
    }

    public void addBalance(AccountEntity userAccount, BigDecimal amount) {
        userAccount.setBalance(userAccount.getBalance().add(amount));

        this.accountRepository.save(userAccount);
    }

    public void createAccount(UserEntity newUser) {
        this.accountRepository.save(AccountEntity.builder()
                .accountNumber(this.accountNumberGenerator.generate())
                .agencyNumber("001")
                .balance(BigDecimal.ZERO)
                .userEntity(newUser)
                .build());
    }
}
