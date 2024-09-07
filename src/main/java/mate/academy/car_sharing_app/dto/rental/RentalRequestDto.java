package mate.academy.car_sharing_app.dto.rental;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RentalRequestDto(
        @NotNull
        Long carId,
        @NotNull
        @Min(value = 1)
        Long rentalDays
) {
}
