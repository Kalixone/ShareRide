package mate.academy.car_sharing_app.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.car.CarDto;
import mate.academy.car_sharing_app.dto.rental.RentalDto;
import mate.academy.car_sharing_app.dto.rental.RentalRequestDto;
import mate.academy.car_sharing_app.dto.rental.RentalSetActualReturnDateRequestDto;
import mate.academy.car_sharing_app.dto.user.UserDto;
import mate.academy.car_sharing_app.exceptions.EntityNotFoundException;
import mate.academy.car_sharing_app.mapper.CarMapper;
import mate.academy.car_sharing_app.mapper.RentalMapper;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.model.Rental;
import mate.academy.car_sharing_app.model.Role;
import mate.academy.car_sharing_app.model.User;
import mate.academy.car_sharing_app.repository.CarRepository;
import mate.academy.car_sharing_app.repository.RentalRepository;
import mate.academy.car_sharing_app.repository.UserRepository;
import mate.academy.car_sharing_app.service.NotificationService;
import mate.academy.car_sharing_app.service.RentalService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final CarMapper carMapper;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public RentalDto rentACar(Long userId, RentalRequestDto rentalRequestDto) {
        Car car = carRepository.findById(rentalRequestDto.carId()).orElseThrow(
                () -> new EntityNotFoundException("Car not found by id: "
                        + rentalRequestDto.carId()));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found by id: " + userId));

        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                mapRoles(user.getRoles())
        );

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

        RentalDto rentalDto = rentalMapper.toDto(rental).withCar(carDto).withUser(userDto);

        String message = String.format("New rental created!\n\nUser:" +
                        " %s %s\nCar: %s %s\nRental Date: %s\nReturn Date: %s",
                user.getFirstName(), user.getLastName(),
                car.getBrand(), car.getModel(),
                rental.getRentalDate(), rental.getReturnDate());

        notificationService.sendNotification(message);

        return rentalDto;
    }

    @Override
    public List<RentalDto> getActiveRentalsByUserId(Long userId) {
        List<Rental> rentals = rentalRepository.findByUserIdAndActualReturnDateIsNull(userId);
        LocalDate now = LocalDate.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + userId));
        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                mapRoles(user.getRoles())
        );

        return rentals.stream()
                .map(rental -> {
                    Car car = carRepository.findById(rental.getCarId())
                            .orElseThrow(() -> new EntityNotFoundException("Car not found by id: "
                                    + rental.getCarId()));

                    CarDto carDto = carMapper.toDto(car);
                    RentalDto rentalDto = rentalMapper.toDto(rental);

                    return rentalDto.withCar(carDto).withUser(userDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    public RentalDto getSpecificRentalByUserId(Long userId, Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found" +
                        " by id: " + rentalId));

        if (!rental.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car" +
                        " not found by id: " + rental.getCarId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + userId));

        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                mapRoles(user.getRoles())
        );

        CarDto carDto = new CarDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType(),
                car.getDailyFee()
        );

        return new RentalDto(
                rental.getId(),
                userDto,
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
        Rental rental = rentalRepository.findById(rentalSetActualReturnDateRequestDto.rentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental not found by ID: "
                        + rentalSetActualReturnDateRequestDto.rentalId()));

        rental.setActualReturnDate(rentalSetActualReturnDateRequestDto.actualReturnDate());

        Rental savedRental = rentalRepository.save(rental);

        carRepository.increaseInventory(rental.getCarId());

        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found" +
                        " by id: " + rental.getCarId()));

        User user = userRepository.findById(rental.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found" +
                        " by id: " + rental.getUserId()));
        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                mapRoles(user.getRoles())
        );

        RentalDto rentalDto = rentalMapper.toDto(savedRental);

        CarDto carDto = carMapper.toDto(car);

        rentalDto = rentalDto.withCar(carDto).withUser(userDto);

        return rentalDto;
    }

    @Override
    public List<RentalDto> checkOverdueRentals() {
        LocalDate now = LocalDate.now();
        List<Rental> byReturnDateBeforeAndActualReturnDateIsNull
                = rentalRepository.findOverdueRentals(now);
        return byReturnDateBeforeAndActualReturnDateIsNull.stream()
                .map(rentalMapper::toDto).collect(Collectors.toList());
    }

    @Scheduled(cron = "0 06 11 * * ?")
    @Override
    public void checkOverdueRentalsAndNotify() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDate.now());

        if (overdueRentals.isEmpty()) {
            notificationService.sendNotification("No rentals overdue today!");
        } else {
            for (Rental rental : overdueRentals) {
                CarDto carDto = carRepository.findById(rental.getCarId())
                        .map(carMapper::toDto)
                        .orElseThrow(() -> new EntityNotFoundException("Car not found" +
                                " by id: " + rental.getCarId()));
                UserDto userDto = userRepository.findById(rental.getUserId())
                        .map(user -> new UserDto(
                                user.getId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                mapRoles(user.getRoles())
                        ))
                        .orElseThrow(() -> new EntityNotFoundException("User not found" +
                                " by id: " + rental.getUserId()));

                RentalDto rentalDto = rentalMapper.toDto(rental)
                        .withCar(carDto)
                        .withUser(userDto);

                String message = String.format(
                        "Overdue Rental!\nID: %d\nUser: %s %s\nCar: %s %s\nReturn Date: %s",
                        rentalDto.id(),
                        rentalDto.user().firstName(),
                        rentalDto.user().lastName(),
                        rentalDto.car().brand(),
                        rentalDto.car().model(),
                        rentalDto.returnDate()
                );
                notificationService.sendNotification(message);
            }
        }
    }

    private Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
    }
}
