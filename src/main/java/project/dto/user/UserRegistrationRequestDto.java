package project.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import project.validation.field.match.FieldMatch;

@Getter
@Setter
@FieldMatch(first = "password", second = "repeatPassword",
        message = "Password must match the repeat password")
public class UserRegistrationRequestDto {
    @NotBlank
    @Length(max = 255)
    private String username;
    @NotBlank
    @Email
    @Length(max = 255)
    private String email;
    @NotBlank
    @Length(min = 8, max = 32)
    private String password;
    @NotBlank
    @Length(min = 8, max = 32)
    private String repeatPassword;
    @NotBlank
    @Length(max = 255)
    private String firstName;
    @NotBlank
    @Length(max = 255)
    private String lastName;
}
