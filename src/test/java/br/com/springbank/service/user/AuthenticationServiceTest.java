package br.com.springbank.service.user;

import br.com.springbank.controller.auth.dto.LoginDto;
import br.com.springbank.controller.auth.dto.LoginResponseDto;
import br.com.springbank.service.exceptions.user.InvalidCredentialsException;
import br.com.springbank.service.token.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private LoginDto loginDto;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto("Matheus", "12345");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        userDetails = new User("Matheus", "12345", authorities);
    }

    @Test
    void deveLogarUsuarioComSucesso() {
        when(this.userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(this.passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(this.tokenService.createToken(any(Authentication.class))).thenReturn("fake-jwt-token");

        LoginResponseDto response = this.authenticationService.loginUser(loginDto);

        Assertions.assertEquals("Matheus", response.username());
        Assertions.assertEquals("Usuario logado com sucesso", response.message());
        Assertions.assertEquals("fake-jwt-token", response.token());
        Assertions.assertTrue(response.status());

        verify(userDetailsService).loadUserByUsername("Matheus");
        verify(passwordEncoder).matches("12345", "12345");
        verify(tokenService).createToken(any(Authentication.class));
    }

    @Test
    void deveRetornarExcecaoSeSenhaEstiverErrada() {
        when(this.userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(this.passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        InvalidCredentialsException ex = Assertions.assertThrows(InvalidCredentialsException.class,
                () -> this.authenticationService.loginUser(loginDto)
        );

        Assertions.assertEquals(InvalidCredentialsException.class, ex.getClass());
        Assertions.assertEquals("Credenciais erradas - Senha incorreta", ex.getMessage());

        verify(userDetailsService).loadUserByUsername("Matheus");
        verify(passwordEncoder).matches("12345", "12345");
        verifyNoInteractions(this.tokenService);
    }
}