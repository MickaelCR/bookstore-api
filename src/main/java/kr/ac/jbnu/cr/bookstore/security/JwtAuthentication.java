package kr.ac.jbnu.cr.bookstore.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class JwtAuthentication extends AbstractAuthenticationToken {

    @Getter
    private final Long userId;
    private final String token;

    public JwtAuthentication(Long userId, String token) {
        super(Collections.emptyList());
        this.userId = userId;
        this.token = token;
        setAuthenticated(true);
    }

    public JwtAuthentication(Long userId, String token, String role) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
        this.userId = userId;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }
}