package project.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.demo.dto.user.UserLoginRequestDto;
import project.demo.dto.user.UserLoginResponseDto;
import project.demo.dto.user.UserRegistrationRequestDto;
import project.demo.dto.user.UserResponseDto;
import project.demo.security.AuthenticationService;
import project.demo.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registration new user.",
            description = "Registration new user. For non login user. Default role is user.")
    public UserResponseDto registration(
            @RequestBody @Valid UserRegistrationRequestDto userRegistrationRequestDto) {
        return userService.registration(userRegistrationRequestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login registered user.",
            description = "Login registered user. For non login user.")
    public UserLoginResponseDto login(
            @RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
        return authenticationService.authentication(userLoginRequestDto);
    }
}
