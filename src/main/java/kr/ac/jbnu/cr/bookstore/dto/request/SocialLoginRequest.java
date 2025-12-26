package kr.ac.jbnu.cr.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLoginRequest {
    @NotBlank(message = "Token is required")
    private String token;
}