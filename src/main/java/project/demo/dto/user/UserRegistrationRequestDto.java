package project.demo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import project.demo.validation.FieldMatch;

@Getter
@Setter
@FieldMatch(first = "password", second = "repeatPassword",
        message = "Password must match the repeat password")
public class UserRegistrationRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Length(min = 8, max = 32)
    private String password;
    @NotBlank
    @Length(min = 8, max = 32)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
