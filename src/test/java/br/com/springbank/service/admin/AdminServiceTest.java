package br.com.springbank.service.admin;

import br.com.springbank.controller.admin.dto.AccountResponseDto;
import br.com.springbank.controller.admin.dto.TransactionsResponseDto;
import br.com.springbank.controller.admin.dto.UsersResponseDto;
import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.account.TransactionEntity;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.domain.repositories.account.TransactionRepository;
import br.com.springbank.domain.repositories.user.UserRepository;
import br.com.springbank.service.exceptions.account.AccountNotFoundException;
import br.com.springbank.service.exceptions.account.UserAccountNotFoundException;
import br.com.springbank.service.exceptions.user.UserAlreadyInactiveException;
import br.com.springbank.service.exceptions.user.UserNotFoundException;
import br.com.springbank.service.exceptions.user.UsernameRequiredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    private UserEntity user1;
    private UserEntity user2;

    private TransactionEntity transaction;

    private AccountEntity accountEntity;

    @BeforeEach
    void setUp() {
        user1 = UserEntity.builder().id(1L).username("Matheus").password("12345").email("matheus@gmail.com").status(StatusEnum.ACTIVE).createdAt(LocalDateTime.now()).build();
        user2 = UserEntity.builder().id(2L).username("Davi").password("12345").email("davi@gmail.com").status(StatusEnum.INACTIVE).createdAt(LocalDateTime.now()).build();
        accountEntity = AccountEntity.builder().id(1L).agencyNumber("001").accountNumber("12345678").userEntity(user1).balance(new BigDecimal("100.00")).createdAt(LocalDateTime.now()).build();
        transaction = TransactionEntity.builder().id(1L).sourceAccount(accountEntity).destinationAccount(null).type(TransactionType.DEPOSIT).amount(new BigDecimal("20.00")).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void deveRetornarListaComTodosOsUsuarios() {
        List<UserEntity> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UsersResponseDto> response = this.adminService.findAllUsers();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(user1.getId(), response.get(0).id());
        assertEquals(user1.getUsername(), response.get(0).username());
        assertEquals(user1.getStatus(), response.get(0).status());

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarUmUsuarioDadoSeuUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));

        UsersResponseDto dto = this.adminService.findUserByUsername("Matheus");

        assertNotNull(dto);
        assertEquals(user1.getId(), dto.id());
        assertEquals(user1.getUsername(), dto.username());
        assertEquals(user1.getStatus(), dto.status());

        verify(userRepository).findByUsername("Matheus");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarUmaExcecaoSeNaoHouverUsername() {
        UsernameRequiredException ex = Assertions.assertThrows(UsernameRequiredException.class, () -> this.adminService.findUserByUsername(""));

        assertEquals(UsernameRequiredException.class, ex.getClass());
        assertEquals("O nome de usuário é obrigatório.", ex.getMessage());

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarUmaExcecaoSeNaoExistirUsuarioDadoUmUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class,
                () -> this.adminService.findUserByUsername("teste"));

        assertEquals(UserNotFoundException.class, ex.getClass());
        assertEquals("Usuário com nome 'teste' não foi encontrado.", ex.getMessage());

        verify(userRepository).findByUsername("teste");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarExcecaoSeUsernameForNull() {
        UsernameRequiredException ex = Assertions.assertThrows(
                UsernameRequiredException.class,
                () -> adminService.findUserByUsername(null)
        );

        assertEquals("O nome de usuário é obrigatório.", ex.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void deveDesabilitarUmUserComSucesso() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsersResponseDto response = this.adminService.disableUser("Matheus");

        assertNotNull(response);
        assertEquals(StatusEnum.INACTIVE, response.status());

        verify(userRepository).findByUsername("Matheus");
        verify(userRepository).save(user1);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarExcecaoSeUsuarioJaEstiverDesativado() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user2));

        UserAlreadyInactiveException ex = Assertions.assertThrows(UserAlreadyInactiveException.class,
                () -> this.adminService.disableUser("Davi")
        );

        assertEquals(UserAlreadyInactiveException.class, ex.getClass());
        assertEquals("Este usuário já está desativado.", ex.getMessage());

        verify(userRepository).findByUsername("Davi");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarExcecaoSeUsuarioNaoExistir() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class,
                () -> this.adminService.disableUser("Davi")
        );

        assertEquals(UserNotFoundException.class, ex.getClass());
        assertEquals("Usuário com nome 'Davi' não foi encontrado.", ex.getMessage());

        verify(userRepository).findByUsername("Davi");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarExcecaoSeUsernameParaDesativarForNull() {
        UsernameRequiredException ex = Assertions.assertThrows(
                UsernameRequiredException.class,
                () -> adminService.disableUser(null)
        );

        assertEquals("O nome de usuário é obrigatório.", ex.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void deveRetornarUmaExcecaoSeNaoHouverUsernameParaSerDesabilitado() {
        UsernameRequiredException ex = Assertions.assertThrows(UsernameRequiredException.class, () -> this.adminService.disableUser(""));

        assertEquals(UsernameRequiredException.class, ex.getClass());
        assertEquals("O nome de usuário é obrigatório.", ex.getMessage());

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deveRetornarTodasAsTransacoes() {
        List<TransactionEntity> transactions = List.of(transaction);
        when(this.transactionRepository.findAll()).thenReturn(transactions);

        List<TransactionsResponseDto> response = this.adminService.findAllTransactions();

        assertNotNull(response);
        assertEquals(1, response.size());

        assertEquals(transaction.getSourceAccount().getAccountNumber(), response.get(0).sourceAccount().accountNumber());
        assertEquals(transaction.getAmount(), response.get(0).amount());
        assertEquals(transaction.getType(), response.get(0).type());

        verify(transactionRepository).findAll();
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void deveRetornarTodasAsContas() {
        List<AccountEntity> contas = List.of(accountEntity);

        when(this.accountRepository.findAll()).thenReturn(contas);

        List<AccountResponseDto> response = this.adminService.findAllAccounts();

        assertNotNull(response);
        assertEquals(1, response.size());

        assertEquals(accountEntity.getAccountNumber(), response.get(0).accountNumber());
        assertEquals(accountEntity.getAgencyNumber(), response.get(0).agencyNumber());
        assertEquals(accountEntity.getId(), response.get(0).id());
        assertEquals(accountEntity.getUserEntity().getUsername(), response.get(0).username());

        verify(accountRepository).findAll();
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveRetornarUmContaDadoSeuNumeroComSucesso() {
        when(this.accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(accountEntity));

        AccountResponseDto response = this.adminService.findAccountByAccountNumber("12345678");

        assertNotNull(response);

        assertEquals(accountEntity.getId(), response.id());
        assertEquals(accountEntity.getAccountNumber(), response.accountNumber());
        assertEquals(accountEntity.getAgencyNumber(), response.agencyNumber());

        verify(accountRepository).findByAccountNumber("12345678");
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveRetornarUmaExcecaoCasoNaoExistaUmaContaComONumeroInformado() {
        when(this.accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        AccountNotFoundException ex = Assertions.assertThrows(AccountNotFoundException.class,
                () -> this.adminService.findAccountByAccountNumber("12345678")
        );

        assertEquals(AccountNotFoundException.class, ex.getClass());
        assertEquals("Conta com número '12345678' não encontrada.", ex.getMessage());

        verify(accountRepository).findByAccountNumber("12345678");
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void deveRetornarUmExcecaoSeONumeroDaContaForNulo() {
        UserAccountNotFoundException ex = Assertions.assertThrows(UserAccountNotFoundException.class,
                () -> this.adminService.findAccountByAccountNumber(null)
        );

        assertEquals(UserAccountNotFoundException.class, ex.getClass());
        assertEquals("O número da conta é obrigatório.", ex.getMessage());

        verifyNoInteractions(accountRepository);
    }

    @Test
    void deveRetornarUmExcecaoSeONumeroDaContaEstiverVazio() {
        UserAccountNotFoundException ex = Assertions.assertThrows(UserAccountNotFoundException.class,
                () -> this.adminService.findAccountByAccountNumber("")
        );

        assertEquals(UserAccountNotFoundException.class, ex.getClass());
        assertEquals("O número da conta é obrigatório.", ex.getMessage());

        verifyNoInteractions(accountRepository);
    }
}