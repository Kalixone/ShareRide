package mate.academy.car_sharing_app.dto;

import mate.academy.car_sharing_app.model.Role;

public record UpdateUserRoleRequestDto(
        Role.RoleName role
) {
}
