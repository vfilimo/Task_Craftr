package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.dto.user.UserResponseDto;
import project.dto.user.UserUpdateRequestDto;
import project.dto.user.UserUpdateRoleDto;
import project.model.User;
import project.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User management.", description = "Endpoints for user management.")
public class UserController {
    private final UserService userService;

    @PutMapping("/{userId}/roleName")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update user role.",
            description = "Update user role. Available for role admin")
    public UserResponseDto updateUserRole(@PathVariable Long userId,
                                          @RequestBody @Valid UserUpdateRoleDto userUpdateRoleDto) {
        return userService.updateUserRole(userId, userUpdateRoleDto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get user info.",
            description = "Get user info. Available for roles user and manager.")
    public UserResponseDto getUserProfileInfo() {
        User user = getUserFromContext();
        return userService.findUserInfo(user.getId());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update user info.",
            description = "Update user info. Available for roles user and manager.")
    public UserResponseDto updateUserInfo(
            @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {
        User user = getUserFromContext();
        return userService.updateUserInfo(user.getId(), userUpdateRequestDto);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
