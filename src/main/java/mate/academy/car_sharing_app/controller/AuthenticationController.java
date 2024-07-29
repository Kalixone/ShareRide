package mate.academy.car_sharing_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.RegisterUserRequestDto;
import mate.academy.car_sharing_app.dto.UserDto;
import mate.academy.car_sharing_app.dto.UserLoginRequestDto;
import mate.academy.car_sharing_app.dto.UserLoginResponseDto;
import mate.academy.car_sharing_app.security.AuthenticationService;
import mate.academy.car_sharing_app.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserDto registerUser(@RequestBody @Valid RegisterUserRequestDto registerUserRequestDto) {
        return userService.register(registerUserRequestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
        return authenticationService.authenticate(userLoginRequestDto);
    }
}
