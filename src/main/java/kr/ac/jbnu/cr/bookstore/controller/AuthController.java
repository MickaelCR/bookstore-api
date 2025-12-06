package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.LoginRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.RefreshTokenRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.RegisterRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.AuthResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.MessageResponse;
import kr.ac.jbnu.cr.bookstore.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (client should discard token)")
    public ResponseEntity<MessageResponse> logout() {
        // JWT is stateless, client should discard the token
        return ResponseEntity.ok(MessageResponse.of("Logged out successfully"));
    }
}