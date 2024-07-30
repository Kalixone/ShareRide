package mate.academy.car_sharing_app.service;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.CarDto;
import mate.academy.car_sharing_app.dto.RentalDto;
import mate.academy.car_sharing_app.dto.RentalRequestDto;
import mate.academy.car_sharing_app.dto.RentalSetActualReturnDateRequestDto;
import mate.academy.car_sharing_app.exceptions.CarNotFoundException;
import mate.academy.car_sharing_app.exceptions.RentalNotFoundException;
import mate.academy.car_sharing_app.mapper.CarMapper;
import mate.academy.car_sharing_app.mapper.RentalMapper;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.model.Rental;
import mate.academy.car_sharing_app.repository.CarRepository;
import mate.academy.car_sharing_app.repository.RentalRepository;
import mate.academy.car_sharing_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final CarMapper carMapper;

    @Transactional
    @Override
    public RentalDto rentACar(Long userId, RentalRequestDto rentalRequestDto) {
        Car car = carRepository.findById(rentalRequestDto.carId()).orElseThrow(
                () -> new CarNotFoundException("Car not found by id: " + rentalRequestDto.carId()));

        Long rentalDays = rentalRequestDto.rentalDays();

        Rental rental = new Rental();
        LocalDate today = LocalDate.now();
        rental.setRentalDate(today);
        rental.setReturnDate(today.plus(rentalDays, ChronoUnit.DAYS));
        rental.setActualReturnDate(null);
        rental.setCarId(car.getId());
        rental.setUserId(userId);

        rentalRepository.save(rental);
        carRepository.decreaseInventory(car.getId());
        CarDto carDto = carMapper.toDto(car);
        RentalDto rentalDto = rentalMapper.toDto(rental);
        return rentalDto.withCar(carDto);

    }

    @Override
    public List<RentalDto> getActiveRentalsByUserId(Long userId) {
        List<Rental> rentals = rentalRepository.findByUserIdAndActualReturnDateIsNull(userId);
        LocalDate now = LocalDate.now();

        return rentals.stream()
                .map(rental -> {
                    Car car = carRepository.findById(rental.getCarId())
                            .orElseThrow(
                                    () -> new CarNotFoundException("" +
                                            "Car not found by id: " + rental.getCarId()));
                    CarDto carDto = carMapper.toDto(car);
                    RentalDto rentalDto = rentalMapper.toDto(rental);
                    boolean isActive = rental.getReturnDate().isAfter(now);
                    return rentalDto.withCar(carDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    public RentalDto getSpecificRentalByUserId(Long userId, Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (!rental.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        CarDto carDto = new CarDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType(),
                car.getDailyFee()
        );

        return new RentalDto(
                rental.getId(),
                rental.getUserId(),
                carDto,
                rental.getRentalDate(),
                rental.getReturnDate(),
                rental.getActualReturnDate(),
                rental.getActualReturnDate() == null
        );
    }

    @Transactional
    @Override
    public RentalDto setActualReturnDate(
            RentalSetActualReturnDateRequestDto rentalSetActualReturnDateRequestDto) {
        Rental rental = rentalRepository.findById(rentalSetActualReturnDateRequestDto
                .rentalId()).orElseThrow(
                () -> new RentalNotFoundException("Rental not found by ID: "
                        + rentalSetActualReturnDateRequestDto.rentalId()));

        // Ustaw faktyczną datę zwrotu
        rental.setActualReturnDate(rentalSetActualReturnDateRequestDto.actualReturnDate());

        // Zapisz zaktualizowany obiekt Rental
        Rental savedRental = rentalRepository.save(rental);

        // Zaktualizuj stan inwentarza samochodu
        carRepository.increaseInventory(rental.getCarId());

        // Pobierz obiekt Car na podstawie carId
        Car car = carRepository.findById(rental.getCarId()).orElse(null);

        // Zamapuj Rental na RentalDto
        RentalDto rentalDto = rentalMapper.toDto(savedRental);

        // Ustaw dane samochodu w RentalDto
        if (car != null) {
            CarDto carDto = carMapper.toDto(car);
            rentalDto = rentalDto.withCar(carDto);
        }

        return rentalDto;
    }
}
