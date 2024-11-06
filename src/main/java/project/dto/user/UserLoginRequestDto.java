package project.dto.user;

public record UserLoginRequestDto(
        String username,
        String password
) {
}
