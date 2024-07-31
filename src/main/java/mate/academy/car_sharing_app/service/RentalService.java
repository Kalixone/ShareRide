package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.RentalDto;
import mate.academy.car_sharing_app.dto.RentalRequestDto;
import mate.academy.car_sharing_app.dto.RentalSetActualReturnDateRequestDto;

import java.util.List;

public interface RentalService {
    RentalDto rentACar(Long userId, RentalRequestDto rentalRequestDto);

    List<RentalDto> getActiveRentalsByUserId(Long userId);

    RentalDto getSpecificRentalByUserId(Long userId, Long rentalId);

    RentalDto setActualReturnDate(
            RentalSetActualReturnDateRequestDto rentalSetActualReturnDateRequestDto);

    List<RentalDto> checkOverdueRentals();

    void checkOverdueRentalsAndNotify();
}
