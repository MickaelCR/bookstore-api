package kr.ac.jbnu.cr.bookstore.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static JwtAuthentication getCurrentAuthentication() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public static Long getCurrentUserId() {
        return getCurrentAuthentication().getUserId();
    }
}