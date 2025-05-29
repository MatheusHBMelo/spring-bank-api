package br.com.springbank.service.token;

import br.com.springbank.service.exceptions.token.ClaimNameRequiredException;
import br.com.springbank.service.exceptions.token.InvalidOrExpiredTokenException;
import br.com.springbank.service.exceptions.token.InvalidTokenSubjectException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {
    public static final int EXPIRATION_MINUTES = 30;
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-03:00");

    @Value("${spring.app.security.secret}")
    private String secret;
    @Value("${spring.app.security.issuer}")
    private String issuer;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(this.secret);
    }

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.issuer)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(LocalDateTime.now().toInstant(ZONE_OFFSET))
                .withExpiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES).toInstant(ZONE_OFFSET))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(LocalDateTime.now().toInstant(ZONE_OFFSET))
                .sign(this.algorithm);
    }

    public DecodedJWT recoveryToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(this.algorithm).withIssuer(this.issuer).build();

            return jwtVerifier.verify(token);
        } catch (JWTVerificationException ex) {
            throw new InvalidOrExpiredTokenException("Token inválido ou expirado.");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        if (decodedJWT == null || decodedJWT.getSubject() == null) {
            throw new InvalidTokenSubjectException("Token inválido: subject ausente.");
        }
        return decodedJWT.getSubject();
    }

    public Claim extractClaimByName(DecodedJWT decodedJWT, String claimName) {
        if (claimName == null || claimName.trim().isEmpty()) {
            throw new ClaimNameRequiredException("Nome do claim não pode ser nulo ou vazio.");
        }
        return decodedJWT.getClaim(claimName);
    }
}
