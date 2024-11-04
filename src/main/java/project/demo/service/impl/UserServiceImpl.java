package project.demo.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.demo.dto.user.UserRegistrationRequestDto;
import project.demo.dto.user.UserResponseDto;
import project.demo.dto.user.UserUpdateRequestDto;
import project.demo.dto.user.UserUpdateRoleDto;
import project.demo.exception.EntityNotFoundException;
import project.demo.exception.RegistrationException;
import project.demo.exception.UserUpdateException;
import project.demo.mapper.UserMapper;
import project.demo.model.Role;
import project.demo.model.User;
import project.demo.repository.RoleRepository;
import project.demo.repository.UserRepository;
import project.demo.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto registration(UserRegistrationRequestDto userRegistrationRequestDto) {
        if (userRepository.findUserByUsernameOrEmail(userRegistrationRequestDto.getUsername(),
                userRegistrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException(String.format("User with username: %s or email: %s "
                    + "is existing", userRegistrationRequestDto.getUsername(),
                    userRegistrationRequestDto.getEmail()));
        }
        User user = userMapper.toEntity(userRegistrationRequestDto);
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));
        Role role = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUserRole(Long userId, UserUpdateRoleDto userUpdateRoleDto) {
        User user = findUserById(userId);
        Role role = roleRepository.findByName(userUpdateRoleDto.roleName());
        user.setRoles(new HashSet<>(Collections.singleton(role)));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto findUserInfo(Long userId) {
        User user = findUserById(userId);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
        if (userRepository.findUserByEmail(userUpdateRequestDto.email()).isPresent()) {
            throw new UserUpdateException(String.format("User with email: %s is existing",
                    userUpdateRequestDto.email()));
        }
        User user = findUserById(userId);
        userMapper.updateUser(user, userUpdateRequestDto);
        return userMapper.toDto(userRepository.save(user));
    }

    private User findUserById(Long userId) {
        return userRepository.findUserById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + userId));
    }
}
