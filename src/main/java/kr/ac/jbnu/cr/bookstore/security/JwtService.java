package kr.ac.jbnu.cr.bookstore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.ac.jbnu.cr.bookstore.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;
    private final long refreshExpiration;
    private final String issuer;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration,
            @Value("${jwt.issuer}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
        this.issuer = issuer;
    }

    /**
     * Create access token
     */
    public String createToken(User user) {
        return Jwts.builder()
                .signWith(key)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .compact();
    }

    /**
     * Create refresh token
     */
    public String createRefreshToken(User user) {
        return Jwts.builder()
                .signWith(key)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .claim("type", "refresh")
                .compact();
    }

    /**
     * Validate token and get user ID
     */
    public Optional<Long> getUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(Long.parseLong(claims.getSubject()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get role from token
     */
    public Optional<String> getRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.ofNullable(claims.get("role", String.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        return getUser(token).isPresent();
    }

    public long getExpiration() {
        return expiration;
    }
}