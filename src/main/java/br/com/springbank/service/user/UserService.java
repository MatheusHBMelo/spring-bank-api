package br.com.springbank.service.user;

import br.com.springbank.controller.auth.dto.RegisterDto;
import br.com.springbank.domain.entities.user.RoleEntity;
import br.com.springbank.domain.entities.user.RoleEnum;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.user.RoleRepository;
import br.com.springbank.domain.repositories.user.UserRepository;
import br.com.springbank.event.RegisterCompletedEvent;
import br.com.springbank.service.account.AccountService;
import br.com.springbank.service.exceptions.token.InvalidOrExpiredTokenException;
import br.com.springbank.service.exceptions.user.RoleNotFoundException;
import br.com.springbank.service.exceptions.user.UserNotFoundException;
import br.com.springbank.service.token.TokenService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountService accountService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuário não encontrado";

    public UserService(UserRepository userRepository, TokenService tokenService, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher,
                       AccountService accountService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.accountService = accountService;
    }

    public UserEntity getAuthenticatedUser(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith("Bearer ")) {
            throw new InvalidOrExpiredTokenException("Token JWT ausente ou inválido");
        }

        String token = header.substring(7);

        DecodedJWT decodedJWT = this.tokenService.recoveryToken(token);

        String username = this.tokenService.extractUsername(decodedJWT);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    @Transactional
    public void registerUser(RegisterDto registerDto) {
        RoleEntity role = this.roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new RoleNotFoundException("Essa role não existe no sistema"));

        UserEntity newUser = this.createUser(registerDto, role);

        this.accountService.createAccount(newUser);

        this.eventPublisher.publishEvent(new RegisterCompletedEvent(newUser));
    }

    private UserEntity createUser(RegisterDto dto, RoleEntity role) {
        return userRepository.save(
                UserEntity.builder()
                        .username(dto.username())
                        .password(passwordEncoder.encode(dto.password()))
                        .email(dto.email())
                        .role(Set.of(role))
                        .status(StatusEnum.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
