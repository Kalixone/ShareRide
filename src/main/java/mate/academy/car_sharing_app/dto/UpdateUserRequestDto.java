package mate.academy.car_sharing_app.dto;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UpdateUserRequestDto(
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @Length(min = 8, max = 20)
        String password
) {
}
