package project.service;

import project.dto.user.UserRegistrationRequestDto;
import project.dto.user.UserResponseDto;
import project.dto.user.UserUpdateRequestDto;
import project.dto.user.UserUpdateRoleDto;

public interface UserService {
    UserResponseDto registration(
            UserRegistrationRequestDto userRegistrationRequestDto);

    UserResponseDto updateUserRole(Long userId, UserUpdateRoleDto userUpdateRoleDto);

    UserResponseDto findUserInfo(Long userId);

    UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto userUpdateRequestDto);
}
