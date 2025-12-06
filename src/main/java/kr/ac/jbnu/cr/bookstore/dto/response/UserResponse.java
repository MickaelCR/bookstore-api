package kr.ac.jbnu.cr.bookstore.dto.response;

import kr.ac.jbnu.cr.bookstore.model.Role;
import kr.ac.jbnu.cr.bookstore.model.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private Role role;
    private LocalDate birthDate;
    private String bio;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .birthDate(user.getBirthDate())
                .bio(user.getBio())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}