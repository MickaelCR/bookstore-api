package kr.ac.jbnu.cr.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AuthorRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String bio;

    private LocalDate birthDate;
}