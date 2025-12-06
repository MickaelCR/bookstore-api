package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.LoginRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.RegisterRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.AuthResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.UserResponse;
import kr.ac.jbnu.cr.bookstore.exception.DuplicateResourceException;
import kr.ac.jbnu.cr.bookstore.exception.UnauthorizedException;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
import kr.ac.jbnu.cr.bookstore.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.createToken(savedUser);
        String refreshToken = jwtService.createRefreshToken(savedUser);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtService.getExpiration(),
                UserResponse.from(savedUser)
        );
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        // Generate tokens
        String accessToken = jwtService.createToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtService.getExpiration(),
                UserResponse.from(user)
        );
    }

    public AuthResponse refresh(String refreshToken) {
        // Validate refresh token
        Long userId = jwtService.getUser(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        // Generate new tokens
        String newAccessToken = jwtService.createToken(user);
        String newRefreshToken = jwtService.createRefreshToken(user);

        return AuthResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtService.getExpiration(),
                UserResponse.from(user)
        );
    }
}