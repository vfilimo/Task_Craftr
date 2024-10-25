package project.demo.dto.user;

public record UserLoginRequestDto(
        String username,
        String password
) {
}
