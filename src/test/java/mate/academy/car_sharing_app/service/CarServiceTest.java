package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.car.CarDto;
import mate.academy.car_sharing_app.dto.car.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.car.UpdateCarRequestDto;
import mate.academy.car_sharing_app.mapper.CarMapper;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.repository.CarRepository;
import mate.academy.car_sharing_app.service.impl.CarServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    private static final Long CAR_ID = 1L;
    private static final String MODEL = "Model S";
    private static final String BRAND = "Tesla";
    private static final Car.TypeCar TYPE = Car.TypeCar.SEDAN;
    private static final int INVENTORY = 10;
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(100.00);
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final int TOTAL_CARS_IN_PAGE = 1;

    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Test
    @DisplayName("Verify createCar() method works")
    public void createCar_ValidRequestDto_ReturnsCarDto() {
        CreateCarRequestDto carRequestDto
                = createCarRequestDto(MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        Car car = createCar(CAR_ID, MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        CarDto carDto = createCarDto(CAR_ID, MODEL, BRAND, TYPE, DAILY_FEE);

        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);
        when(carRepository.save(car)).thenReturn(car);

        CarDto savedCarDto = carService.createCar(carRequestDto);

        assertThat(savedCarDto).isEqualTo(carDto);
        verify(carRepository).save(car);
        verify(carMapper).toModel(carRequestDto);
        verify(carMapper).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify getById() method works")
    public void getCarById_ValidId_ReturnsCarDto() {
        Car car = createCar(CAR_ID, MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        CarDto carDto = createCarDto(CAR_ID, MODEL, BRAND, TYPE, DAILY_FEE);

        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto carById = carService.getCarById(CAR_ID);

        assertThat(carById).isEqualTo(carDto);
        verify(carRepository).findById(CAR_ID);
        verify(carMapper).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify getAll() method works")
    public void getAll_ValidPageable_ReturnsAllCars() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Car car = createCar(CAR_ID, MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        CarDto carDto = createCarDto(CAR_ID, MODEL, BRAND, TYPE, DAILY_FEE);
        Page<Car> carPage = new PageImpl<>(List.of(car), pageable, TOTAL_CARS_IN_PAGE);

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(car)).thenReturn(carDto);

        List<CarDto> result = carService.getAll(pageable);

        assertThat(result).containsExactly(carDto);
        verify(carRepository).findAll(pageable);
        verify(carMapper).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify deleteById() method works")
    public void deleteById_ValidId_DeletesCar() {
        carService.deleteById(CAR_ID);

        verify(carRepository).deleteById(CAR_ID);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Verify updateCar() method works")
    public void updateCar_ValidCarRequestDto_ReturnsCarDto() {
        UpdateCarRequestDto updateCarRequestDto
                = createUpdateCarRequestDto(MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        Car car = createCar(CAR_ID, MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        CarDto carDto = createCarDto(CAR_ID, MODEL, BRAND, TYPE, DAILY_FEE);

        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(carMapper.updateModel(updateCarRequestDto, car)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto result = carService.update(CAR_ID, updateCarRequestDto);

        assertThat(result).isEqualTo(carDto);
        verify(carRepository).findById(CAR_ID);
        verify(carRepository).save(car);
        verify(carMapper).updateModel(updateCarRequestDto, car);
        verify(carMapper).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    private CarDto createCarDto(Long id, String model, String brand,
                                Car.TypeCar type, BigDecimal dailyFee) {
        return new CarDto(id, model, brand, type, dailyFee);
    }

    private CreateCarRequestDto createCarRequestDto(String model, String brand,
                                                    Car.TypeCar type, int inventory,
                                                    BigDecimal dailyFee) {
        return new CreateCarRequestDto(model, brand, type, inventory, dailyFee);
    }

    private UpdateCarRequestDto createUpdateCarRequestDto(String model, String brand,
                                                          Car.TypeCar type, int inventory,
                                                          BigDecimal dailyFee) {
        return new UpdateCarRequestDto(model, brand, type, inventory, dailyFee);
    }

    private Car createCar(Long id, String model, String brand, Car.TypeCar type,
                          int inventory, BigDecimal dailyFee) {
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setBrand(brand);
        car.setType(type);
        car.setInventory(inventory);
        car.setDailyFee(dailyFee);
        return car;
    }
}
