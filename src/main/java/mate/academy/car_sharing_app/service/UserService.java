package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.user.RegisterUserRequestDto;
import mate.academy.car_sharing_app.dto.user.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.dto.user.UserDto;
import mate.academy.car_sharing_app.dto.user.UserUpdateResponseDto;
import mate.academy.car_sharing_app.dto.user.UpdateUserRequestDto;

public interface UserService {
    UserDto register(RegisterUserRequestDto registerUserRequestDto);

    UserDto getProfileInfo(String username);

    UserUpdateResponseDto updateProfile(String username, UpdateUserRequestDto updateUserRequestDto);

    UserDto updateRole(Long id, UpdateUserRoleRequestDto updateUserRoleRequestDto);
}
