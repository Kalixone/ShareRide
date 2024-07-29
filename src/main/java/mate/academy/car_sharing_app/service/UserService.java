package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.RegisterUserRequestDto;
import mate.academy.car_sharing_app.dto.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.dto.UserDto;
import mate.academy.car_sharing_app.dto.UserUpdateResponseDto;
import mate.academy.car_sharing_app.dto.UpdateUserRequestDto;

public interface UserService {
    UserDto register(RegisterUserRequestDto registerUserRequestDto);

    UserDto getProfileInfo(String username);

    UserUpdateResponseDto updateProfile(String username, UpdateUserRequestDto updateUserRequestDto);

    UserDto updateRole(Long id, UpdateUserRoleRequestDto updateUserRoleRequestDto);
}

