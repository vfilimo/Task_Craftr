package project.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Length(max = 255)
        String username,
        @NotBlank
        @Length(max = 32)
        String password
) {
}
