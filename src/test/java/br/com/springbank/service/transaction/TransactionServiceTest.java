package br.com.springbank.service.transaction;

import br.com.springbank.controller.transaction.dto.DepositRequestDto;
import br.com.springbank.controller.transaction.dto.StatementResponseDto;
import br.com.springbank.controller.transaction.dto.TransferRequestDto;
import br.com.springbank.controller.transaction.dto.WithdrawRequestDto;
import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.account.TransactionEntity;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.domain.repositories.account.TransactionRepository;
import br.com.springbank.event.DepositCompletedEvent;
import br.com.springbank.event.TransferCompletedEvent;
import br.com.springbank.event.WithdrawCompletedEvent;
import br.com.springbank.service.account.AccountService;
import br.com.springbank.service.exceptions.transaction.InsufficientBalanceException;
import br.com.springbank.service.exceptions.transaction.InvalidTransactionAmountException;
import br.com.springbank.service.exceptions.user.InactiveUserException;
import br.com.springbank.service.user.UserService;
import br.com.springbank.validator.TransactionValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserService userService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionValidator transactionValidator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private UserEntity user;

    private UserEntity userSecond;

    private AccountEntity senderAccount;

    private AccountEntity receiverAccount;

    private TransferRequestDto transferRequestDto;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder().id(1L).username("Matheus").email("matheus@email.com").password("12345").status(StatusEnum.ACTIVE).createdAt(LocalDateTime.now()).build();
        userSecond = UserEntity.builder().id(1L).username("Davi").email("davi@email.com").password("12345").status(StatusEnum.ACTIVE).createdAt(LocalDateTime.now()).build();
        senderAccount = AccountEntity.builder().id(1L).accountNumber("12345678").agencyNumber("001").balance(new BigDecimal("100.00")).userEntity(user).createdAt(LocalDateTime.now()).build();
        receiverAccount = AccountEntity.builder().id(1L).accountNumber("87654321").agencyNumber("001").balance(new BigDecimal("10.00")).userEntity(userSecond).createdAt(LocalDateTime.now()).build();
        transferRequestDto = new TransferRequestDto(new BigDecimal("20.00"), "87654321");
    }

    @Test
    void deveFazerTransferenciaComSucesso() {
        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);
        when(this.accountService.getUserAccountByAccountNumber(anyString())).thenReturn(receiverAccount);

        this.transactionService.transfer(transferRequestDto);

        verify(transactionValidator).validatePositiveAmount(transferRequestDto.amount(), "Transferência");
        verify(transactionValidator).validateSufficientBalance(senderAccount.getBalance(), transferRequestDto.amount(), "Transferência");

        verify(accountService).subtractBalance(senderAccount, transferRequestDto.amount());
        verify(accountService).addBalance(receiverAccount, transferRequestDto.amount());

        verify(transactionRepository).save(any(TransactionEntity.class));
        verify(eventPublisher).publishEvent(any(TransferCompletedEvent.class));
    }

    @Test
    void deveRetornarExcecaoSeUsuarioForInativo() {
        userSecond.setStatus(StatusEnum.INACTIVE);

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);
        when(this.accountService.getUserAccountByAccountNumber(anyString())).thenReturn(receiverAccount);

        InactiveUserException ex = Assertions.assertThrows(InactiveUserException.class,
                () -> this.transactionService.transfer(transferRequestDto)
        );

        Assertions.assertEquals(InactiveUserException.class, ex.getClass());
        Assertions.assertEquals("Você não pode fazer transação para uma conta com usuário inativo.", ex.getMessage());

        verifyNoInteractions(transactionValidator);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveRetornarExcecaoSeValorDaTransferenciaForZero() {
        TransferRequestDto invalidTransfer = new TransferRequestDto(BigDecimal.ZERO, "87654321");

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);
        when(this.accountService.getUserAccountByAccountNumber(anyString())).thenReturn(receiverAccount);

        doThrow(new InvalidTransactionAmountException("O valor do Transferência deve ser no mínimo R$0.01.")).when(this.transactionValidator).validatePositiveAmount(any(), any());

        InvalidTransactionAmountException ex = Assertions.assertThrows(InvalidTransactionAmountException.class,
                () -> this.transactionService.transfer(invalidTransfer)
        );

        Assertions.assertEquals(InvalidTransactionAmountException.class, ex.getClass());
        Assertions.assertEquals("O valor do Transferência deve ser no mínimo R$0.01.", ex.getMessage());

        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveRetornarExcecaoSeSaldoForInsuficiente() {
        TransferRequestDto invalidTransfer = new TransferRequestDto(new BigDecimal("2000.00"), "87654321");

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);
        when(this.accountService.getUserAccountByAccountNumber(anyString())).thenReturn(receiverAccount);

        doThrow(new InsufficientBalanceException("Saldo insuficiente para Transferência")).when(this.transactionValidator).validateSufficientBalance(any(), any(), any());

        InsufficientBalanceException ex = Assertions.assertThrows(InsufficientBalanceException.class,
                () -> this.transactionService.transfer(invalidTransfer)
        );

        Assertions.assertEquals(InsufficientBalanceException.class, ex.getClass());
        Assertions.assertEquals("Saldo insuficiente para Transferência", ex.getMessage());

        verify(transactionValidator).validatePositiveAmount(invalidTransfer.amount(), "Transferência");
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveFazerDepositoComSucesso() {
        DepositRequestDto depositValid = new DepositRequestDto(new BigDecimal("100.00"));

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any())).thenReturn(senderAccount);

        doAnswer(invocation -> {
            AccountEntity conta = invocation.getArgument(0);
            BigDecimal valor = invocation.getArgument(1);
            conta.setBalance(conta.getBalance().add(valor));
            return null;
        }).when(this.accountService).addBalance(any(AccountEntity.class), any(BigDecimal.class));

        this.transactionService.deposit(depositValid);

        verify(this.transactionValidator).validatePositiveAmount(depositValid.amount(), "Deposito");
        verify(this.accountService).addBalance(senderAccount, depositValid.amount());
        verify(this.transactionRepository).save(any(TransactionEntity.class));
        verify(this.eventPublisher).publishEvent(any(DepositCompletedEvent.class));

        Assertions.assertEquals(new BigDecimal("200.00"), senderAccount.getBalance());
    }

    @Test
    void deveRetornarExcecaoNoDepositoSeUsuarioForInativo() {
        user.setStatus(StatusEnum.INACTIVE);

        DepositRequestDto depositValid = new DepositRequestDto(new BigDecimal("100.00"));

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any())).thenReturn(senderAccount);

        InactiveUserException ex = Assertions.assertThrows(InactiveUserException.class,
                () -> this.transactionService.deposit(depositValid)
        );

        Assertions.assertEquals(InactiveUserException.class, ex.getClass());
        Assertions.assertEquals("Você não pode fazer transação para uma conta com usuário inativo.", ex.getMessage());

        verifyNoInteractions(transactionValidator);
        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveRetornarExcecaoNoDepositoSeValorForInvalido() {
        DepositRequestDto depositInvalid = new DepositRequestDto(BigDecimal.ZERO);

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any())).thenReturn(senderAccount);

        doThrow(new InvalidTransactionAmountException("O valor do deposito deve ser no mínimo R$0.01."))
                .when(this.transactionValidator).validatePositiveAmount(any(), any());

        InvalidTransactionAmountException ex = Assertions.assertThrows(InvalidTransactionAmountException.class,
                () -> this.transactionService.deposit(depositInvalid)
        );

        Assertions.assertEquals(InvalidTransactionAmountException.class, ex.getClass());
        Assertions.assertEquals("O valor do deposito deve ser no mínimo R$0.01.", ex.getMessage());

        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveFazerSaqueComSucesso() {
        WithdrawRequestDto dto = new WithdrawRequestDto(new BigDecimal("50.00"));

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);

        BigDecimal pastBalance = senderAccount.getBalance();

        doAnswer(invocation -> {
            AccountEntity account = invocation.getArgument(0);
            BigDecimal amount = invocation.getArgument(1);
            account.setBalance(account.getBalance().subtract(amount));
            return null;
        }).when(this.accountService).subtractBalance(senderAccount, dto.amount());

        this.transactionService.withdraw(dto);

        verify(this.transactionValidator).validatePositiveAmount(dto.amount(), "Saque");
        verify(this.transactionValidator).validateSufficientBalance(pastBalance, dto.amount(), "Saque");
        verify(this.accountService).subtractBalance(senderAccount, dto.amount());
        verify(this.transactionRepository).save(any(TransactionEntity.class));
        verify(this.eventPublisher).publishEvent(any(WithdrawCompletedEvent.class));

        Assertions.assertEquals(new BigDecimal("50.00"), senderAccount.getBalance());
    }

    @Test
    void deveRetornarExcecaoAoSacarSeUsuarioForinativo() {
        user.setStatus(StatusEnum.INACTIVE);
        WithdrawRequestDto dto = new WithdrawRequestDto(new BigDecimal("50.00"));

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);

        InactiveUserException ex = Assertions.assertThrows(InactiveUserException.class,
                () -> this.transactionService.withdraw(dto)
        );

        Assertions.assertEquals(InactiveUserException.class, ex.getClass());
        Assertions.assertEquals("Você não pode fazer transação para uma conta com usuário inativo.", ex.getMessage());

        verifyNoInteractions(transactionValidator);
        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveRetornarExcecaoAoSacarSeValorForInvalido() {
        WithdrawRequestDto invalidWithdraw = new WithdrawRequestDto(BigDecimal.ZERO);

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);

        doThrow(new InvalidTransactionAmountException("O valor do Saque deve ser no mínimo R$0.01."))
                .when(this.transactionValidator).validatePositiveAmount(invalidWithdraw.amount(), "Saque");

        InvalidTransactionAmountException ex = Assertions.assertThrows(InvalidTransactionAmountException.class,
                () -> this.transactionService.withdraw(invalidWithdraw)
        );

        Assertions.assertEquals(InvalidTransactionAmountException.class, ex.getClass());
        Assertions.assertEquals("O valor do Saque deve ser no mínimo R$0.01.", ex.getMessage());

        verifyNoMoreInteractions(transactionValidator);
        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveRetornarExcecaoAoSacarSeSaldoForInsuficiente() {
        WithdrawRequestDto validWithdraw = new WithdrawRequestDto(new BigDecimal("50.00"));

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);

        doThrow(new InsufficientBalanceException("Saldo insuficiente para saque"))
                .when(this.transactionValidator).validateSufficientBalance(senderAccount.getBalance(), validWithdraw.amount(), "Saque");

        InsufficientBalanceException ex = Assertions.assertThrows(InsufficientBalanceException.class,
                () -> this.transactionService.withdraw(validWithdraw)
        );

        Assertions.assertEquals(InsufficientBalanceException.class, ex.getClass());
        Assertions.assertEquals("Saldo insuficiente para saque", ex.getMessage());

        verifyNoMoreInteractions(accountService);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveRetornarOExtratoDeTrasacoes() {
        List<TransactionEntity> transactions = List.of(new TransactionEntity(1L, TransactionType.TRANSFER, new BigDecimal("50.00"), senderAccount, receiverAccount, LocalDateTime.now()));

        when(this.userService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(user);
        when(this.accountService.getUserAccount(any(UserEntity.class))).thenReturn(senderAccount);
        when(this.transactionRepository.findAllByAccountId(anyLong())).thenReturn(transactions);

        List<StatementResponseDto> transactionEntities = this.transactionService.bankStatement();

        Assertions.assertNotNull(transactionEntities);
        Assertions.assertEquals(1, transactionEntities.size());

        Assertions.assertEquals(transactionEntities.get(0).type(), transactions.get(0).getType());
        Assertions.assertEquals(transactionEntities.get(0).amount(), transactions.get(0).getAmount());
        Assertions.assertEquals(transactionEntities.get(0).sourceAccount().accountNumber(), transactions.get(0).getSourceAccount().getAccountNumber());
        Assertions.assertEquals(transactionEntities.get(0).destinationAccount().accountNumber(), transactions.get(0).getDestinationAccount().getAccountNumber());
    }
}