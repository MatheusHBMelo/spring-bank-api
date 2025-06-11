package br.com.springbank.validator;

import br.com.springbank.service.exceptions.transaction.InsufficientBalanceException;
import br.com.springbank.service.exceptions.transaction.InvalidTransactionAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionValidatorTest {
    private TransactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TransactionValidator();
    }

    @Test
    void deveValidarValorPositivoComSucesso() {
        assertDoesNotThrow(() -> validator.validatePositiveAmount(new BigDecimal("10.00"), "Depósito"));
    }

    @Test
    void deveLancarExcecaoParaValorNulo() {
        InvalidTransactionAmountException ex = assertThrows(
                InvalidTransactionAmountException.class,
                () -> validator.validatePositiveAmount(null, "Depósito")
        );
        assertEquals("O valor do depósito deve ser no mínimo R$0.01.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaValorNegativo() {
        InvalidTransactionAmountException ex = assertThrows(
                InvalidTransactionAmountException.class,
                () -> validator.validatePositiveAmount(new BigDecimal("-5.00"), "Saque")
        );
        assertEquals("O valor do saque deve ser no mínimo R$0.01.", ex.getMessage());
    }

    @Test
    void deveValidarSaldoSuficienteComSucesso() {
        assertDoesNotThrow(() -> validator.validateSufficientBalance(new BigDecimal("100.00"), new BigDecimal("20.00"), "Transferência"));
    }

    @Test
    void deveLancarExcecaoParaSaldoInsuficiente() {
        InsufficientBalanceException ex = assertThrows(
                InsufficientBalanceException.class,
                () -> validator.validateSufficientBalance(new BigDecimal("10.00"), new BigDecimal("20.00"), "Transferência")
        );
        assertEquals("Saldo insuficiente para Transferência", ex.getMessage());
    }
}