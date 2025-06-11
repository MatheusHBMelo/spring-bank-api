package br.com.springbank.utils;

import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.service.exceptions.account.AccountNumberGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccountNumberGeneratorTest {
    private AccountRepository accountRepository;
    private AccountNumberGenerator generator;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        generator = new AccountNumberGenerator(accountRepository);
    }

    @Test
    void deveGerarNumeroDeContaQuandoNaoExisteConflito() {
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        String accountNumber = generator.generate();

        assertNotNull(accountNumber);
        assertEquals(8, accountNumber.length());
        verify(accountRepository, atLeastOnce()).existsByAccountNumber(accountNumber);
    }

    @Test
    void deveTentarNovamenteQuandoNumeroJaExiste() {
        when(accountRepository.existsByAccountNumber(anyString()))
                .thenReturn(true, true, true, false);

        String accountNumber = generator.generate();

        assertNotNull(accountNumber);
        assertEquals(8, accountNumber.length());
        verify(accountRepository, times(4)).existsByAccountNumber(anyString());
    }

    @Test
    void deveLancarExcecaoQuandoNaoConseguirGerarNumeroUnico() {
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(true);

        AccountNumberGenerationException ex = assertThrows(
                AccountNumberGenerationException.class,
                () -> generator.generate()
        );

        assertEquals("Não foi possível gerar um número de conta único após muitas tentativas.", ex.getMessage());
        verify(accountRepository, times(10)).existsByAccountNumber(anyString());
    }
}