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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AccountService accountService;

    @Mock
    private HttpServletRequest request;

    private UserEntity user;

    private RoleEntity role;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder().id(1L).username("Matheus").email("matheus@email.com").password("12345")
                .role(Set.of()).status(StatusEnum.ACTIVE).createdAt(LocalDateTime.now()).build();

        role = RoleEntity.builder().id(1L).name(RoleEnum.USER).permissions(Set.of()).build();
    }

    @Test
    void deveRetornarUsuarioAutenticado() {
        String token = "mytoken";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(this.request.getHeader(anyString())).thenReturn("Bearer " + token);
        when(this.tokenService.recoveryToken(anyString())).thenReturn(decodedJWT);
        when(this.tokenService.extractUsername(decodedJWT)).thenReturn("Matheus");
        when(this.userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserEntity authenticatedUser = this.userService.getAuthenticatedUser(this.request);

        Assertions.assertEquals("Matheus", authenticatedUser.getUsername());
        Assertions.assertEquals("matheus@email.com", authenticatedUser.getEmail());
        Assertions.assertEquals("12345", authenticatedUser.getPassword());
        Assertions.assertEquals(StatusEnum.ACTIVE, authenticatedUser.getStatus());
        Assertions.assertEquals(1L, authenticatedUser.getId());

        verify(request).getHeader("Authorization");
        verify(tokenService).recoveryToken(token);
        verify(tokenService).extractUsername(decodedJWT);
        verify(userRepository).findByUsername("Matheus");
    }

    @Test
    void deveRetornarExcecaoSeHeaderForNulo() {
        when(this.request.getHeader(anyString())).thenReturn(null);

        InvalidOrExpiredTokenException ex = Assertions.assertThrows(InvalidOrExpiredTokenException.class,
                () -> this.userService.getAuthenticatedUser(this.request)
        );

        Assertions.assertEquals(InvalidOrExpiredTokenException.class, ex.getClass());
        Assertions.assertEquals("Token JWT ausente ou inválido", ex.getMessage());

        verifyNoInteractions(tokenService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void deveRetornarExcecaoSeHeaderForInvalido() {
        when(this.request.getHeader(anyString())).thenReturn("tokentoken");

        InvalidOrExpiredTokenException ex = Assertions.assertThrows(InvalidOrExpiredTokenException.class,
                () -> this.userService.getAuthenticatedUser(this.request)
        );

        Assertions.assertEquals(InvalidOrExpiredTokenException.class, ex.getClass());
        Assertions.assertEquals("Token JWT ausente ou inválido", ex.getMessage());

        verifyNoInteractions(tokenService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void deveRetornarExcecaoSeUsuarioNaoExistir() {
        String token = "validToken";
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.recoveryToken(token)).thenReturn(decodedJWT);
        when(tokenService.extractUsername(decodedJWT)).thenReturn("matheus");
        when(userRepository.findByUsername("matheus")).thenReturn(Optional.empty());

        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class,
                () -> this.userService.getAuthenticatedUser(this.request)
        );

        Assertions.assertEquals(UserNotFoundException.class, ex.getClass());
        Assertions.assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void deveRegistrarUserComSucesso() {
        RegisterDto dto = new RegisterDto("Matheus", "12345", "matheus@email.com");

        when(this.roleRepository.findByName(any(RoleEnum.class))).thenReturn(Optional.of(role));
        when(this.passwordEncoder.encode("12345")).thenReturn("encriptedPass");

        this.userService.registerUser(dto);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();

        assertEquals("Matheus", savedUser.getUsername());
        assertEquals("encriptedPass", savedUser.getPassword());
        assertEquals("matheus@email.com", savedUser.getEmail());
        assertTrue(savedUser.getRole().contains(role));
        assertEquals(StatusEnum.ACTIVE, savedUser.getStatus());

        verify(this.userRepository).save(any(UserEntity.class));
        verify(eventPublisher).publishEvent(any(RegisterCompletedEvent.class));
    }

    @Test
    void deveRetornarExdecaoSeRoleNaoExistir() {
        RegisterDto dto = new RegisterDto("Matheus", "12345", "matheus@email.com");

        when(this.roleRepository.findByName(any(RoleEnum.class))).thenReturn(Optional.empty());

        RoleNotFoundException ex = Assertions.assertThrows(RoleNotFoundException.class,
                () -> this.userService.registerUser(dto)
        );

        assertEquals(RoleNotFoundException.class, ex.getClass());
        assertEquals("Essa role não existe no sistema", ex.getMessage());

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(accountService);
        verifyNoInteractions(eventPublisher);
    }
}