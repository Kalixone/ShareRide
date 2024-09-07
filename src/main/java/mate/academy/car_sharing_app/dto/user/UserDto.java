package mate.academy.car_sharing_app.dto.user;

import java.util.Set;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {
}
