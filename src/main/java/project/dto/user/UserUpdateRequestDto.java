package project.dto.user;

import jakarta.validation.constraints.Email;

public record UserUpdateRequestDto(
        @Email
        String email,
        String firstName,
        String lastName
) {
}
