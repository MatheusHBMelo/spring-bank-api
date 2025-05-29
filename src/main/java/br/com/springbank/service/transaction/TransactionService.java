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
import br.com.springbank.service.user.UserService;
import br.com.springbank.validator.TransactionValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final HttpServletRequest request;
    private final UserService userService;
    private final AccountService accountService;
    private final TransactionValidator transactionValidator;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionService(TransactionRepository transactionRepository,
                              HttpServletRequest request, UserService userService, AccountService accountService,
                              TransactionValidator transactionValidator, ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.request = request;
        this.userService = userService;
        this.accountService = accountService;
        this.transactionValidator = transactionValidator;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void transfer(TransferRequestDto transferRequestDto) {
        UserEntity senderUser = this.userService.getAuthenticatedUser(this.request);

        AccountEntity senderAccount = this.accountService.getUserAccount(senderUser);

        AccountEntity receiverAccount = this.accountService.getUserAccountByAccountNumber(transferRequestDto.receiverAccountNumber());

        this.validateStatusAccount(receiverAccount.getUserEntity().getStatus());

        this.transactionValidator.validatePositiveAmount(transferRequestDto.amount(), "Transferência");
        this.transactionValidator.validateSufficientBalance(senderAccount.getBalance(), transferRequestDto.amount(), "Transferência");

        this.accountService.subtractBalance(senderAccount, transferRequestDto.amount());
        this.accountService.addBalance(receiverAccount, transferRequestDto.amount());

        this.createTransaction(senderAccount, receiverAccount, transferRequestDto.amount(), TransactionType.TRANSFER);

        this.eventPublisher.publishEvent(new TransferCompletedEvent(senderUser, transferRequestDto.amount(), receiverAccount.getAccountNumber()));
    }

    @Transactional
    public void deposit(DepositRequestDto depositRequestDto) {
        UserEntity user = this.userService.getAuthenticatedUser(this.request);

        AccountEntity userAccount = this.accountService.getUserAccount(user);

        this.validateStatusAccount(userAccount.getUserEntity().getStatus());

        this.transactionValidator.validatePositiveAmount(depositRequestDto.amount(), "Deposito");

        this.accountService.addBalance(userAccount, depositRequestDto.amount());

        this.createTransaction(userAccount, null, depositRequestDto.amount(), TransactionType.DEPOSIT);

        this.eventPublisher.publishEvent(new DepositCompletedEvent(user, depositRequestDto.amount()));
    }

    @Transactional
    public void withdraw(WithdrawRequestDto withdrawRequestDto) {
        UserEntity user = this.userService.getAuthenticatedUser(this.request);

        AccountEntity userAccount = this.accountService.getUserAccount(user);

        this.validateStatusAccount(userAccount.getUserEntity().getStatus());

        this.transactionValidator.validatePositiveAmount(withdrawRequestDto.amount(), "Saque");
        this.transactionValidator.validateSufficientBalance(userAccount.getBalance(), withdrawRequestDto.amount(), "Saque");

        this.accountService.subtractBalance(userAccount, withdrawRequestDto.amount());

        this.createTransaction(userAccount, null, withdrawRequestDto.amount(), TransactionType.WITHDRAW);

        this.eventPublisher.publishEvent(new WithdrawCompletedEvent(user, withdrawRequestDto.amount()));
    }

    public List<StatementResponseDto> bankStatement() {
        UserEntity user = this.userService.getAuthenticatedUser(this.request);

        AccountEntity userAccount = this.accountService.getUserAccount(user);

        List<TransactionEntity> transactions = this.transactionRepository.findAllByAccountId(userAccount.getId());

        return transactions.stream()
                .map(StatementResponseDto::fromEntity)
                .toList();
    }

    private void createTransaction(AccountEntity sourceAccount, AccountEntity destinationAccount, BigDecimal amount, TransactionType type) {
        TransactionEntity transaction = TransactionEntity.builder()
                .type(type)
                .amount(amount)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .build();

        transactionRepository.save(transaction);
    }

    private void validateStatusAccount(StatusEnum status) {
        if (status == StatusEnum.INACTIVE) {
            throw new RuntimeException("Você não pode fazer transação para uma conta com usuario inativo.");
        }
    }
}
