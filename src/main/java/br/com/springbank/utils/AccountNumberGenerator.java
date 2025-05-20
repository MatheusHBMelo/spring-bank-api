package br.com.springbank.utils;

import br.com.springbank.domain.repositories.account.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountNumberGenerator {
    private final AccountRepository accountRepository;

    public AccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generate() {
        String number;
        do {
            number = String.format("%08d", ThreadLocalRandom.current().nextInt(0, 100_000_000));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
