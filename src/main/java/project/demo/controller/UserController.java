package project.demo.controller;

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
import project.demo.dto.user.UserResponseDto;
import project.demo.dto.user.UserUpdateRequestDto;
import project.demo.dto.user.UserUpdateRoleDto;
import project.demo.model.User;
import project.demo.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{userId}/roleName")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponseDto updateUserRole(@PathVariable Long userId,
                                          @RequestBody @Valid UserUpdateRoleDto userUpdateRoleDto) {
        return userService.updateUserRole(userId, userUpdateRoleDto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    public UserResponseDto getUserProfileInfo() {
        User user = getUserFromContext();
        return userService.findUserInfo(user.getId());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
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
