package mate.academy.car_sharing_app.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.UpdateUserRequestDto;
import mate.academy.car_sharing_app.dto.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.dto.UserDto;
import mate.academy.car_sharing_app.dto.UserUpdateResponseDto;
import mate.academy.car_sharing_app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDto getProfileInfo(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        return userService.getProfileInfo(username);
    }

    @PutMapping("/me")
    public UserUpdateResponseDto updateProfile(@RequestBody @Valid UpdateUserRequestDto requestDto,
                                               Authentication authentication) {
        String email = authentication.getName();
        return userService.updateProfile(email, requestDto);
    }

    @PutMapping("/{id}/role")
    public UserDto updateRole(
                              @PathVariable Long id,
                              @RequestBody @Valid
                              UpdateUserRoleRequestDto updateUserRoleRequestDto) {
        return userService.updateRole(id, updateUserRoleRequestDto);
    }
}
