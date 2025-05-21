package br.com.springbank.service.transaction;

import br.com.springbank.controller.transaction.dto.DepositRequestDto;
import br.com.springbank.controller.transaction.dto.TransferRequestDto;
import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.account.TransactionEntity;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.enums.TransactionType;
import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.domain.repositories.account.TransactionRepository;
import br.com.springbank.domain.repositories.user.UserRepository;
import br.com.springbank.service.token.TokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TokenService tokenService;
    private final HttpServletRequest request;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, TokenService tokenService, HttpServletRequest request, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.tokenService = tokenService;
        this.request = request;
        this.userRepository = userRepository;
    }

    public void transfer(TransferRequestDto transferRequestDto) {
        String token = request.getHeader("AUTHORIZATION").substring(7);
        DecodedJWT decodedJWT = this.tokenService.recoveryToken(token);
        String username = this.tokenService.extractUsername(decodedJWT);

        UserEntity senderUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário remetente não encontrado"));

        AccountEntity senderAccount = accountRepository.findByUserEntity(senderUser)
                .orElseThrow(() -> new RuntimeException("Conta do remetente não encontrada"));

        AccountEntity receiverAccount = this.accountRepository.findByAccountNumber(transferRequestDto.receiverAccountNumber())
                .orElseThrow(() -> new RuntimeException("Não existe conta com esse nome"));

        if (transferRequestDto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor da transferencia deve ser no minimo R$0.01");
        }

        if (senderAccount.getBalance().compareTo(transferRequestDto.amount()) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequestDto.amount()));
        receiverAccount.setBalance(receiverAccount.getBalance().add(transferRequestDto.amount()));

        this.accountRepository.save(senderAccount);
        this.accountRepository.save(receiverAccount);

        TransactionEntity transaction = TransactionEntity.builder()
                .type(TransactionType.TRANSFER)
                .amount(transferRequestDto.amount())
                .sourceAccount(senderAccount)
                .destinationAccount(receiverAccount)
                .build();

        this.transactionRepository.save(transaction);
    }

    public void deposit(DepositRequestDto depositRequestDto) {
        String token = request.getHeader("AUTHORIZATION").substring(7);
        DecodedJWT decodedJWT = this.tokenService.recoveryToken(token);
        String username = this.tokenService.extractUsername(decodedJWT);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        AccountEntity userAccount = accountRepository.findByUserEntity(user)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (depositRequestDto.amount() == null || depositRequestDto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor do deposito deve ser no minimo R$0.01");
        }

        userAccount.setBalance(userAccount.getBalance().add(depositRequestDto.amount()));

        this.accountRepository.save(userAccount);

        TransactionEntity transaction = TransactionEntity.builder()
                .type(TransactionType.DEPOSIT)
                .amount(depositRequestDto.amount())
                .sourceAccount(userAccount)
                .destinationAccount(null)
                .build();

        this.transactionRepository.save(transaction);
    }
}
