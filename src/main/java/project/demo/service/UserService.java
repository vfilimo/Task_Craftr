package project.demo.service;

import project.demo.dto.user.UserRegistrationRequestDto;
import project.demo.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto registration(
            UserRegistrationRequestDto userRegistrationRequestDto);
}
