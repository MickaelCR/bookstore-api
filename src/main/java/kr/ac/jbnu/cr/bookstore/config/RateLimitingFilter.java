package kr.ac.jbnu.cr.bookstore.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long TIME_WINDOW_MS = 60000;
    private static final int RETRY_AFTER_SECONDS = 60;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIP = getClientIP(request);
        long now = System.currentTimeMillis();

        RateLimitInfo rateLimitInfo = requestCounts.compute(clientIP, (key, info) -> {
            if (info == null || now - info.windowStart > TIME_WINDOW_MS) {
                return new RateLimitInfo(now, new AtomicInteger(1));
            }
            info.count.incrementAndGet();
            return info;
        });

        int remaining = Math.max(0, MAX_REQUESTS_PER_MINUTE - rateLimitInfo.count.get());
        long resetTimestamp = (rateLimitInfo.windowStart + TIME_WINDOW_MS) / 1000;

        response.setHeader("RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
        response.setHeader("RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("RateLimit-Reset", String.valueOf(resetTimestamp));

        if (rateLimitInfo.count.get() > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", String.valueOf(RETRY_AFTER_SECONDS));

            response.getWriter().write("""
                {
                    "error": "rate_limited",
                    "retryAfterSeconds": %d
                }
                """.formatted(RETRY_AFTER_SECONDS));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateLimitInfo {
        long windowStart;
        AtomicInteger count;

        RateLimitInfo(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}