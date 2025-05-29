package br.com.springbank.service.user;

import br.com.springbank.controller.auth.dto.LoginDto;
import br.com.springbank.controller.auth.dto.LoginResponseDto;
import br.com.springbank.service.exceptions.user.InvalidCredentialsException;
import br.com.springbank.service.token.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(TokenService tokenService, UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto loginUser(LoginDto loginDto) {
        String username = loginDto.username();
        String password = loginDto.password();

        Authentication authentication = this.authenticateUser(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = this.tokenService.createToken(authentication);

        return new LoginResponseDto(username, "Usuario logado com sucesso", token, true);
    }

    private Authentication authenticateUser(String username, String password) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new InvalidCredentialsException("Credenciais erradas - Senha incorreta");
        }

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }
}
