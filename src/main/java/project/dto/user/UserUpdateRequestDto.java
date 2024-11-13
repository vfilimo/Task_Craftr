package project.dto.user;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record UserUpdateRequestDto(
        @Email
        @Length(max = 255)
        String email,
        @Length(max = 255)
        String firstName,
        @Length(max = 255)
        String lastName
) {
}
