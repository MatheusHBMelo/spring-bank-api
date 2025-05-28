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
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            String number = String.format("%08d", ThreadLocalRandom.current().nextInt(0, 100_000_000));
            if (!accountRepository.existsByAccountNumber(number)) {
                return number;
            }
        }
        throw new IllegalStateException("Não foi possível gerar um número de conta único após " + maxAttempts + " tentativas.");
    }
}
