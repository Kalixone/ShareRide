package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.car.CarDto;
import mate.academy.car_sharing_app.dto.rental.RentalDto;
import mate.academy.car_sharing_app.dto.rental.RentalRequestDto;
import mate.academy.car_sharing_app.dto.rental.RentalSetActualReturnDateRequestDto;
import mate.academy.car_sharing_app.dto.user.UserDto;
import mate.academy.car_sharing_app.mapper.CarMapper;
import mate.academy.car_sharing_app.mapper.RentalMapper;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.model.Rental;
import mate.academy.car_sharing_app.model.Role;
import mate.academy.car_sharing_app.model.User;
import mate.academy.car_sharing_app.repository.CarRepository;
import mate.academy.car_sharing_app.repository.RentalRepository;
import mate.academy.car_sharing_app.repository.UserRepository;
import mate.academy.car_sharing_app.service.impl.RentalServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    private static final Long CAR_ID = 1L;
    private static final String BRAND = "Tesla";
    private static final String MODEL = "Model S";
    private static final Car.TypeCar CAR_TYPE = Car.TypeCar.SEDAN;
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(100);
    private static final Role.RoleName ROLE = Role.RoleName.CUSTOMER;
    private static final Long RENTAL_ID = 1L;
    private static final Long ROLE_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final int INVENTORY = 5;
    private static final String USER_EMAIL = "dirk@example.com";
    private static final String USER_FIRST_NAME = "Piotr";
    private static final String USER_LAST_NAME = "Mockar";
    private static final Long RENTAL_DAYS = 15L;
    private static final LocalDate RETURN_DATE = LocalDate.now().plusDays(10);

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CarMapper carMapper;

    @Test
    @DisplayName("Verify rentACar() method works")
    public void rentACar_ValidRequestDto_ReturnsRentalDto() {
        Car car = createTestCar();
        User user = createTestUser();
        Rental rental = createTestRental(user, car);

        CarDto carDto = new CarDto(
                car.getId(), car.getModel(), car.getBrand(), car.getType(), car.getDailyFee()
        );

        UserDto userDto = new UserDto(
                user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), new HashSet<>(user.getRoles().stream()
                .map(role -> role.getRoleName().name()).toList())
        );

        RentalDto rentalDto = new RentalDto(
                rental.getId(), userDto, carDto, rental.getRentalDate(),
                rental.getReturnDate(), rental.getActualReturnDate(),
                rental.getActualReturnDate() == null
        );

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(carMapper.toDto(car)).thenReturn(carDto);
        doReturn(rentalDto).when(rentalMapper).toDto(any(Rental.class));
        doNothing().when(notificationService).sendNotification(anyString());

        RentalDto result = rentalService.rentACar(user.getId(),
                new RentalRequestDto(car.getId(), RENTAL_DAYS));

        assertThat(result).isEqualTo(rentalDto);
        verify(notificationService).sendNotification(anyString());
    }

    @Test
    @DisplayName("Verify getActiveRentalsByUserId() method works")
    public void getActiveRentalsByUserId_ActiveRentals_ReturnsRentalDtoList() {
        Car car = createTestCar();
        User user = createTestUser();
        Rental rental = createTestRental(user, car);

        CarDto carDto = new CarDto(
                car.getId(), car.getModel(), car.getBrand(),
                car.getType(), car.getDailyFee()
        );

        UserDto userDto = new UserDto(
                user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), new HashSet<>(user.getRoles().stream()
                .map(role -> role.getRoleName().name()).toList())
        );

        RentalDto rentalDto = new RentalDto(
                rental.getId(), userDto, carDto, rental.getRentalDate(),
                rental.getReturnDate(), rental.getActualReturnDate(),
                rental.getActualReturnDate() == null
        );

        when(rentalRepository.findByUserIdAndActualReturnDateIsNull(USER_ID))
                .thenReturn(Arrays.asList(rental));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);
        when(carMapper.toDto(car)).thenReturn(carDto);

        List<RentalDto> result = rentalService.getActiveRentalsByUserId(USER_ID);

        assertThat(result).containsExactly(rentalDto);
        verify(rentalRepository).findByUserIdAndActualReturnDateIsNull(USER_ID);
        verify(userRepository).findById(USER_ID);
        verify(carRepository).findById(CAR_ID);
        verify(rentalMapper).toDto(rental);
        verify(carMapper).toDto(car);
        verifyNoMoreInteractions(rentalRepository, userRepository,
                carRepository, rentalMapper, carMapper);
    }

    @Test
    @DisplayName("Verify getSpecificRentalByUserId() method works")
    public void getSpecificRentalByUserId_ValidRequest_ReturnsRentalDto() {
        Car car = createTestCar();
        User user = createTestUser();
        Rental rental = createTestRental(user, car);

        CarDto carDto = new CarDto(
                car.getId(), car.getModel(), car.getBrand(),
                car.getType(), car.getDailyFee()
        );

        UserDto userDto = new UserDto(
                user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), new HashSet<>(user.getRoles().stream()
                .map(role -> role.getRoleName().name()).toList())
        );

        RentalDto rentalDto = new RentalDto(
                rental.getId(), userDto, carDto, rental.getRentalDate(),
                rental.getReturnDate(), rental.getActualReturnDate(),
                rental.getActualReturnDate() == null
        );

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        RentalDto result = rentalService.getSpecificRentalByUserId(USER_ID, RENTAL_ID);

        assertThat(result).isEqualTo(rentalDto);
        verify(rentalRepository).findById(RENTAL_ID);
        verify(carRepository).findById(CAR_ID);
        verify(userRepository).findById(USER_ID);
        verifyNoMoreInteractions(rentalRepository, carRepository, userRepository);
    }

    @Test
    @DisplayName("Verify setActualReturnDate() method works")
    public void setActualReturnDate_ValidRequest_ReturnsUpdatedRentalDto() {
        RentalSetActualReturnDateRequestDto requestDto
                = new RentalSetActualReturnDateRequestDto(RENTAL_ID, RETURN_DATE);

        Car car = createTestCar();
        User user = createTestUser();
        Rental existingRental = createTestRental(user, car);

        Rental updatedRental = new Rental();
        updatedRental.setId(existingRental.getId());
        updatedRental.setCarId(existingRental.getCarId());
        updatedRental.setUserId(existingRental.getUserId());
        updatedRental.setRentalDate(existingRental.getRentalDate());
        updatedRental.setReturnDate(existingRental.getReturnDate());
        updatedRental.setActualReturnDate(RETURN_DATE);

        CarDto carDto = new CarDto(
                car.getId(), car.getModel(), car.getBrand(),
                car.getType(), car.getDailyFee()
        );

        UserDto userDto = new UserDto(
                user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), new HashSet<>(user.getRoles().stream()
                .map(role -> role.getRoleName().name()).toList())
        );

        RentalDto rentalDto = new RentalDto(
                updatedRental.getId(), userDto, carDto, updatedRental.getRentalDate(),
                updatedRental.getReturnDate(), updatedRental.getActualReturnDate(),
                updatedRental.getActualReturnDate() == null
        );

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(existingRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(updatedRental);
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(carMapper.toDto(car)).thenReturn(carDto);
        when(rentalMapper.toDto(updatedRental)).thenReturn(rentalDto);

        RentalDto result = rentalService.setActualReturnDate(requestDto);

        assertThat(result).isEqualTo(rentalDto);
        verify(rentalRepository).findById(RENTAL_ID);
        verify(rentalRepository).save(updatedRental);
        verify(carRepository).increaseInventory(CAR_ID);
        verify(carRepository).findById(CAR_ID);
        verify(userRepository).findById(USER_ID);
        verify(carMapper).toDto(car);
        verify(rentalMapper).toDto(updatedRental);
        verifyNoMoreInteractions(rentalRepository, carRepository,
                userRepository, carMapper, rentalMapper);
    }

    private Car createTestCar() {
        Car car = new Car();
        car.setId(CAR_ID);
        car.setBrand(BRAND);
        car.setModel(MODEL);
        car.setType(CAR_TYPE);
        car.setInventory(INVENTORY);
        car.setDailyFee(DAILY_FEE);
        return car;
    }

    private User createTestUser() {
        Role role = new Role();
        role.setId(ROLE_ID);
        role.setRoleName(ROLE);

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        return user;
    }

    private Rental createTestRental(User user, Car car) {
        Rental rental = new Rental();
        rental.setId(RENTAL_ID);
        rental.setCarId(car.getId());
        rental.setUserId(user.getId());
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(RENTAL_DAYS));
        rental.setActualReturnDate(null);
        return rental;
    }
}
