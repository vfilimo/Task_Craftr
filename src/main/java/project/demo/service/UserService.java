package project.demo.service;

import project.demo.dto.user.UserRegistrationRequestDto;
import project.demo.dto.user.UserResponseDto;
import project.demo.dto.user.UserUpdateRequestDto;
import project.demo.dto.user.UserUpdateRoleDto;

public interface UserService {
    UserResponseDto registration(
            UserRegistrationRequestDto userRegistrationRequestDto);

    UserResponseDto updateUserRole(Long userId, UserUpdateRoleDto userUpdateRoleDto);

    UserResponseDto findUserInfo(Long userId);

    UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto userUpdateRequestDto);
}
