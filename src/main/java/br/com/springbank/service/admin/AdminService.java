package br.com.springbank.service.admin;

import br.com.springbank.controller.admin.dto.UserRequestDto;
import br.com.springbank.controller.admin.dto.UsersResponseDto;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.domain.repositories.account.TransactionRepository;
import br.com.springbank.domain.repositories.user.UserRepository;
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

    public UsersResponseDto findUserByUsername(UserRequestDto userRequestDto) {
        UserEntity user = this.userRepository.findByUsername(userRequestDto.username()).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        return UsersResponseDto.fromUserEntity(user);
    }
}
