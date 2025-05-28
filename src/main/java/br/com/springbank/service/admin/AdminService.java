package br.com.springbank.service.admin;

import br.com.springbank.controller.admin.dto.AccountResponseDto;
import br.com.springbank.controller.admin.dto.TransactionsResponseDto;
import br.com.springbank.controller.admin.dto.UsersResponseDto;
import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.account.TransactionEntity;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.domain.repositories.account.TransactionRepository;
import br.com.springbank.domain.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AdminService(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<UsersResponseDto> findAllUsers() {
        List<UserEntity> users = this.userRepository.findAll();

        return users.stream().map(UsersResponseDto::fromUserEntity).toList();
    }

    public UsersResponseDto findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("O username é obrigatório.");
        }

        UserEntity user = this.userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Este usuario não foi encontrado."));

        return UsersResponseDto.fromUserEntity(user);
    }

    @Transactional
    public UsersResponseDto disableUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("O username é obrigatório.");
        }

        UserEntity user = this.userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Este usuario não foi encontrado."));

        if (user.getStatus().equals(StatusEnum.INACTIVE)) {
            throw new RuntimeException("Este usuário já está desativado.");
        }

        user.setStatus(StatusEnum.INACTIVE);

        this.userRepository.save(user);

        return UsersResponseDto.fromUserEntity(user);
    }

    public List<TransactionsResponseDto> findAllTransactions() {
        List<TransactionEntity> transactions = this.transactionRepository.findAll();

        return transactions.stream().map(TransactionsResponseDto::fromTransactionEntity).toList();
    }

    public List<AccountResponseDto> findAllAccounts() {
        List<AccountEntity> accounts = this.accountRepository.findAll();

        return accounts.stream().map(AccountResponseDto::fromAccountEntity).toList();
    }

    public AccountResponseDto findAccountByAccountNumber(String numberAccount) {
        if (numberAccount == null || numberAccount.trim().isEmpty()) {
            throw new RuntimeException("O número da conta é obrigatório.");
        }

        AccountEntity account = this.accountRepository.findByAccountNumber(numberAccount)
                .orElseThrow(() -> new RuntimeException("Esta conta não foi encontrada."));

        return AccountResponseDto.fromAccountEntity(account);
    }
}
