package project.demo.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.demo.dto.user.UserRegistrationRequestDto;
import project.demo.dto.user.UserResponseDto;
import project.demo.exception.RegistrationException;
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
}
