package mate.academy.car_sharing_app.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateUserRequestDto(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @Length(min = 8, max = 20)
        String password
) {
}
