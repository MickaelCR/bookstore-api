package kr.ac.jbnu.cr.bookstore.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phoneNumber;

    private LocalDate birthDate;

    @Size(max = 1000, message = "Bio must be less than 1000 characters")
    private String bio;
}