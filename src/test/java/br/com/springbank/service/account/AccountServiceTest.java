package br.com.springbank.service.account;

import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.service.exceptions.account.AccountNotFoundException;
import br.com.springbank.service.exceptions.account.UserAccountNotFoundException;
import br.com.springbank.utils.AccountNumberGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    private UserEntity user;

    private Optional<AccountEntity> account;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder().id(1L).username("Matheus").password("12345").email("matheus@email.com").build();
        account = Optional.of(AccountEntity.builder().id(1L).accountNumber("12345678").agencyNumber("001").balance(new BigDecimal("100.00")).userEntity(user).createdAt(LocalDateTime.now()).build());
    }

    @Test
    void deveRetornaUmaContaDeUsuarioComSucessoAoBuscarPorUsuario() {
        when(accountRepository.findByUserEntity(user)).thenReturn(account);

        AccountEntity accountBD = accountService.getUserAccount(user);

        Assertions.assertNotNull(accountBD);
        assertEquals(AccountEntity.class, accountBD.getClass());
        assertAll("Validações de account",
                () -> assertEquals(1L, accountBD.getId()),
                () -> assertEquals("12345678", accountBD.getAccountNumber()),
                () -> assertEquals("001", accountBD.getAgencyNumber()),
                () -> assertEquals(user, accountBD.getUserEntity()),
                () -> assertEquals(new BigDecimal("100.00"), accountBD.getBalance())
        );

        verify(accountRepository).findByUserEntity(user);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveRetornarUmaExcecaoAoNaoEncontrarUmContaDeUsuarioPeloUsuario() {
        when(accountRepository.findByUserEntity(user)).thenReturn(Optional.empty());

        UserAccountNotFoundException ex = Assertions.assertThrows(UserAccountNotFoundException.class,
                () -> accountService.getUserAccount(user)
        );

        assertEquals(UserAccountNotFoundException.class, ex.getClass());
        assertEquals("Conta associada ao usuário não encontrada.", ex.getMessage());
        verify(accountRepository).findByUserEntity(user);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveRetornaUmaContaDeUsuarioComSucessoAoBuscarPorNumeroDaConta() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(account);

        AccountEntity accountBD = accountService.getUserAccountByAccountNumber("12345678");

        Assertions.assertNotNull(accountBD);
        assertEquals(AccountEntity.class, accountBD.getClass());
        assertAll("Validações de account",
                () -> assertEquals(1L, accountBD.getId()),
                () -> assertEquals("12345678", accountBD.getAccountNumber()),
                () -> assertEquals("001", accountBD.getAgencyNumber()),
                () -> assertEquals(user, accountBD.getUserEntity()),
                () -> assertEquals(new BigDecimal("100.00"), accountBD.getBalance())
        );

        verify(accountRepository).findByAccountNumber("12345678");
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveRetornaUmaExcecaoAoBuscarContaPorNumeroDaConta() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        String accountNumber = "12345678";
        AccountNotFoundException ex = Assertions.assertThrows(AccountNotFoundException.class,
                () -> accountService.getUserAccountByAccountNumber(accountNumber)
        );

        assertEquals(AccountNotFoundException.class, ex.getClass());
        assertEquals("Conta com número '" + accountNumber + "' não encontrada.", ex.getMessage());
        verify(accountRepository).findByAccountNumber("12345678");
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveSubtrairOSaldoDeUmaContaComSucesso() {
        BigDecimal valorParaSubtrair = new BigDecimal("50.00");

        accountService.subtractBalance(account.get(), valorParaSubtrair);

        assertEquals(new BigDecimal("50.00"), account.get().getBalance());
        verify(accountRepository).save(account.get());
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveAdicionarOSaldoDeUmaContaComSucesso() {
        BigDecimal valorParaAdicionar = new BigDecimal("50.00");

        accountService.addBalance(account.get(), valorParaAdicionar);

        assertEquals(new BigDecimal("150.00"), account.get().getBalance());
        verify(accountRepository).save(account.get());
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveCriarUmaContaCorretamente() {
        String numeroGerado = "12345678";
        when(accountNumberGenerator.generate()).thenReturn(numeroGerado);

        ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);

        accountService.createAccount(user);

        verify(accountNumberGenerator).generate();
        verify(accountRepository).save(captor.capture());

        AccountEntity contaCriada = captor.getValue();

        assertAll("Validação da conta criada",
                () -> assertEquals("001", contaCriada.getAgencyNumber()),
                () -> assertEquals(BigDecimal.ZERO, contaCriada.getBalance()),
                () -> assertEquals(user, contaCriada.getUserEntity()),
                () -> assertEquals(numeroGerado, contaCriada.getAccountNumber())
        );

        verifyNoMoreInteractions(accountRepository, accountNumberGenerator);
    }
}