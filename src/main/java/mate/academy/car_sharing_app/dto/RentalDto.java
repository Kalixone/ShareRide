package mate.academy.car_sharing_app.dto;

import java.time.LocalDate;

public record RentalDto(
        Long id,
        UserDto user,
        CarDto car,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate,
        boolean isActive
) {

    public RentalDto withCar(CarDto car) {
        return new RentalDto(id, user, car, rentalDate, returnDate, actualReturnDate, isActive);
    }

    public RentalDto withUser(UserDto user) {
        return new RentalDto(id, user, car, rentalDate, returnDate, actualReturnDate, isActive);
    }
}
