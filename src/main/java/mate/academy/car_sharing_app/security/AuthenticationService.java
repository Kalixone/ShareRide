package mate.academy.car_sharing_app.security;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.user.UserLoginRequestDto;
import mate.academy.car_sharing_app.dto.user.UserLoginResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        String token = jwtUtil.generateToken(authenticate.getName());
        return new UserLoginResponseDto(token);
    }
}
