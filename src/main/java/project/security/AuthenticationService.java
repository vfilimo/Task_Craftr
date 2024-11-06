package project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import project.dto.user.UserLoginRequestDto;
import project.dto.user.UserLoginResponseDto;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authentication(UserLoginRequestDto userLoginRequestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDto.username(), userLoginRequestDto.password()));
        String token = jwtUtil.generateToken(authenticate.getName());
        return new UserLoginResponseDto(token);
    }
}
