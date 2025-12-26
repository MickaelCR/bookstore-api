package kr.ac.jbnu.cr.bookstore.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import kr.ac.jbnu.cr.bookstore.dto.request.LoginRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.RegisterRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.SocialLoginRequest;
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
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

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
        Long userId = jwtService.getUser(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        String newAccessToken = jwtService.createToken(user);
        String newRefreshToken = jwtService.createRefreshToken(user);

        return AuthResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtService.getExpiration(),
                UserResponse.from(user)
        );
    }

    @Transactional
    public AuthResponse socialLogin(SocialLoginRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getToken());

            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String uid = decodedToken.getUid();

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = User.builder()
                        .email(email)
                        .username(name != null ? name : "User_" + uid.substring(0, 5))
                        .passwordHash(passwordEncoder.encode("SOCIAL_" + uid))
                        .build();

                user = userRepository.save(user);
            }

            if (user.getIsActive() != null && !user.getIsActive()) {
                throw new UnauthorizedException("Account is deactivated");
            }

            String accessToken = jwtService.createToken(user);
            String refreshToken = jwtService.createRefreshToken(user);

            return AuthResponse.of(
                    accessToken,
                    refreshToken,
                    jwtService.getExpiration(),
                    UserResponse.from(user)
            );

        } catch (FirebaseAuthException e) {
            throw new UnauthorizedException("Invalid Firebase Token: " + e.getMessage());
        }
    }
}