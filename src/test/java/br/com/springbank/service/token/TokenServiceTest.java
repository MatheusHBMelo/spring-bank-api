package br.com.springbank.service.token;

import br.com.springbank.service.exceptions.token.ClaimNameRequiredException;
import br.com.springbank.service.exceptions.token.InvalidOrExpiredTokenException;
import br.com.springbank.service.exceptions.token.InvalidTokenSubjectException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenServiceTest {
    @InjectMocks
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @Mock
    GrantedAuthority grantedAuthority;

    private final String secret = "test-secret";
    private final String issuer = "test-issuer";
    private final Algorithm algorithm = Algorithm.HMAC256(secret);

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", secret);
        ReflectionTestUtils.setField(tokenService, "issuer", issuer);
        tokenService.init();
    }

    @Test
    void deveCriarTokenComSucesso() {
        String username = "Matheus";
        String authority = "ROLE_ADMIN";

        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(authority));

        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        String token = this.tokenService.createToken(authentication);

        assertNotNull(token);

        DecodedJWT decodedJWT = JWT.decode(token);

        assertEquals(issuer, decodedJWT.getIssuer());
        assertEquals(username, decodedJWT.getSubject());
        assertEquals(authority, decodedJWT.getClaim("authorities").asString());
        assertNotNull(decodedJWT.getId());
    }

    @Test
    void deveLancarExcecaoQuandoORecoveryTokenReceberTokenInvalido() {
        String invalidToken = "test.12345.teste";

        InvalidOrExpiredTokenException ex = Assertions.assertThrows(InvalidOrExpiredTokenException.class,
                () -> this.tokenService.recoveryToken(invalidToken)
        );

        assertEquals(InvalidOrExpiredTokenException.class, ex.getClass());
        assertEquals("Token inválido ou expirado.", ex.getMessage());
    }

    @Test
    void deveRetornarSubjectComSucessoQuandoTokenForValido() {

        DecodedJWT jwt = mock(DecodedJWT.class);
        when(jwt.getSubject()).thenReturn("Matheus");

        String subject = this.tokenService.extractUsername(jwt);

        assertNotNull(subject);
        assertEquals(jwt.getSubject(), subject);
    }

    @Test
    void deveRetornarExcecaoQuandoSubjectForNulo() {
        DecodedJWT jwt = mock(DecodedJWT.class);
        when(jwt.getSubject()).thenReturn(null);

        InvalidTokenSubjectException ex = Assertions.assertThrows(InvalidTokenSubjectException.class,
                () -> this.tokenService.extractUsername(jwt));

        assertEquals(InvalidTokenSubjectException.class, ex.getClass());
        assertEquals("Token inválido: subject ausente.", ex.getMessage());
    }

    @Test
    void deveRetornarExcecaoQuandoDecodedForNulo() {
        InvalidTokenSubjectException ex = assertThrows(
                InvalidTokenSubjectException.class,
                () -> tokenService.extractUsername(null)
        );

        assertEquals(InvalidTokenSubjectException.class, ex.getClass());
        assertEquals("Token inválido: subject ausente.", ex.getMessage());
    }

    @Test
    void deveRetornarClaimComSucesso() {
        Claim mockClaim = mock(Claim.class);
        DecodedJWT jwt = mock(DecodedJWT.class);

        when(jwt.getClaim("authorities")).thenReturn(mockClaim);

        Claim result = this.tokenService.extractClaimByName(jwt, "authorities");

        assertEquals(mockClaim, result);
    }

    @Test
    void deveRetornarExcecaoQuandoClaimForVazio() {
        DecodedJWT jwt = mock(DecodedJWT.class);

        ClaimNameRequiredException ex = assertThrows(ClaimNameRequiredException.class, () -> {
            tokenService.extractClaimByName(jwt, "");
        });

        assertEquals(ClaimNameRequiredException.class, ex.getClass());
        assertEquals("Nome do claim não pode ser nulo ou vazio.", ex.getMessage());
    }

    @Test
    void deveRetornarExcecaoQuandoClaimForNulo() {
        DecodedJWT jwt = mock(DecodedJWT.class);

        ClaimNameRequiredException ex = assertThrows(ClaimNameRequiredException.class, () -> {
            tokenService.extractClaimByName(jwt, null);
        });

        assertEquals(ClaimNameRequiredException.class, ex.getClass());
        assertEquals("Nome do claim não pode ser nulo ou vazio.", ex.getMessage());
    }
}