package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.rental.RentalDto;
import mate.academy.car_sharing_app.dto.rental.RentalRequestDto;
import mate.academy.car_sharing_app.dto.rental.RentalSetActualReturnDateRequestDto;
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
