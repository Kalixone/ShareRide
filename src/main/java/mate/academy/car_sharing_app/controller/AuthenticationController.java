package mate.academy.car_sharing_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Authentication management", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(
            summary = "Register a new user",
            description = "Register a new user in the system. The request body should contain user" +
                    " details such as email, password, and other necessary information." +
                    " The response will include user details after successful registration."
    )
    public UserDto registerUser(@RequestBody @Valid RegisterUserRequestDto registerUserRequestDto) {
        return userService.register(registerUserRequestDto);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate a user",
            description = "Authenticate a user by verifying their credentials." +
                    " The request body should include the user's email and password." +
                    " On successful authentication," +
                    " a token will be returned to be used for accessing secured endpoints."
    )
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
        return authenticationService.authenticate(userLoginRequestDto);
    }
}
