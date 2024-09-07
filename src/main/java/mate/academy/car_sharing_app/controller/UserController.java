package mate.academy.car_sharing_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.user.UpdateUserRequestDto;
import mate.academy.car_sharing_app.dto.user.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.dto.user.UserDto;
import mate.academy.car_sharing_app.dto.user.UserUpdateResponseDto;
import mate.academy.car_sharing_app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing user profiles and roles")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile information",
            description = "Retrieve the profile information of the currently authenticated user." +
                    " Returns details such as username, email, and other profile attributes."
    )
    public UserDto getProfileInfo(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.getProfileInfo(username);
    }

    @PutMapping("/me")
    @Operation(
            summary = "Update current user profile information",
            description = "Update the profile information of the currently authenticated user." +
                    " Requires the user to be authenticated and provide valid data in the request."
    )
    public UserUpdateResponseDto updateProfile(@RequestBody @Valid UpdateUserRequestDto requestDto,
                                               Authentication authentication) {
        String email = authentication.getName();
        return userService.updateProfile(email, requestDto);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(
            summary = "Update user role by ID",
            description = "Update the role of a user identified by their ID." +
                    " This operation requires admin privileges." +
                    " The request body must include the new role details."
    )
    public UserDto updateRole(
                              @PathVariable Long id,
                              @RequestBody @Valid
                              UpdateUserRoleRequestDto updateUserRoleRequestDto) {
        return userService.updateRole(id, updateUserRoleRequestDto);
    }
}
