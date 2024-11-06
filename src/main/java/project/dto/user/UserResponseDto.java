package project.dto.user;

import java.util.List;

public record UserResponseDto(
        String username,
        Long id,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {
}
