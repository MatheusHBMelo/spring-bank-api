package br.com.springbank.service.user;

import br.com.springbank.controller.auth.dto.LoginDto;
import br.com.springbank.controller.auth.dto.LoginResponseDto;
import br.com.springbank.controller.auth.dto.RegisterDto;
import br.com.springbank.domain.entities.account.AccountEntity;
import br.com.springbank.domain.entities.user.RoleEntity;
import br.com.springbank.domain.entities.user.RoleEnum;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.account.AccountRepository;
import br.com.springbank.domain.repositories.user.RoleRepository;
import br.com.springbank.domain.repositories.user.UserRepository;
import br.com.springbank.service.email.EmailService;
import br.com.springbank.service.token.TokenService;
import br.com.springbank.utils.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final EmailService emailService;

    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                                  TokenService tokenService, AccountRepository accountRepository, AccountNumberGenerator accountNumberGenerator,
                                  EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity usercreated = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("O username " + username + " não existe no sistema"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        usercreated.getRole()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName().name()))));

        usercreated.getRole()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(usercreated.getUsername(), usercreated.getPassword(), authorities);
    }

    @Transactional
    public void registerUser(RegisterDto registerDto){
        String username = registerDto.username();
        String password = registerDto.password();
        String email = registerDto.email();

        RoleEntity role = this.roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new RuntimeException("Essa role não existe no sistema"));

        UserEntity newUser = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(Set.of(role))
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity userEntityCreated = this.userRepository.save(newUser);

        AccountEntity newAccount = AccountEntity.builder()
                .accountNumber(this.accountNumberGenerator.generate())
                .agencyNumber("001")
                .balance(BigDecimal.ZERO)
                .userEntity(userEntityCreated)
                .build();

        this.accountRepository.save(newAccount);

        this.emailService.sendEmail(email, "Confirmação de conta", "Confirmamos que sua conta foi criada com sucesso no sistema, Sr." + username);
    }

    public LoginResponseDto loginUser(LoginDto loginDto) {
        String username = loginDto.username();
        String password = loginDto.password();

        Authentication authentication = this.validateCredentials(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = this.tokenService.createToken(authentication);

        return new LoginResponseDto(username, "Usuario logado com sucesso", token, true);
    }

    private Authentication validateCredentials(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new RuntimeException("Credenciais erradas - Username não existe");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new RuntimeException("Credenciais erradas - Senha incorreta");
        }

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }
}
