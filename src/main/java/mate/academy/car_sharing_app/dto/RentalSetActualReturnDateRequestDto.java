package mate.academy.car_sharing_app.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RentalSetActualReturnDateRequestDto(
        @NotNull
        Long rentalId,
        @NotNull
        LocalDate actualReturnDate
) {
}
