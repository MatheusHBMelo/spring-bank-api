package br.com.springbank.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
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
    @Value("${spring.app.security.secret}")
    private String secret;
    @Value("${spring.app.security.issuer}")
    private String issuer;

    public String createToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.secret);

        String username = authentication.getName();
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.issuer)
                .withSubject(username)
                .withClaim("authorities", authorities)
                .withIssuedAt(LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                .withExpiresAt(LocalDateTime.now().plusMinutes(5).toInstant(ZoneOffset.of("-03:00")))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(LocalDateTime.now().toInstant(ZoneOffset.of("-03:00")))
                .sign(algorithm);
    }

    public DecodedJWT recoveryToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);

            JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(this.issuer).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            return decodedJWT;
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException("Esse token não está valido -  Não autorizado.");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject().toString();
    }

    public Claim extractClaimByName(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }
}
