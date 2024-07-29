package mate.academy.car_sharing_app.dto;

import mate.academy.car_sharing_app.model.Role;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role.RoleName role
) {
}
