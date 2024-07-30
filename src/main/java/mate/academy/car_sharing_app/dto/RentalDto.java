package mate.academy.car_sharing_app.dto;

import java.time.LocalDate;

public record RentalDto(
        Long id,
        Long userId,
        CarDto car,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate,
        boolean isActive
) {
    public RentalDto withCar(CarDto car) {
        return new RentalDto(id, userId, car, rentalDate, returnDate, actualReturnDate, isActive);
    }
}
