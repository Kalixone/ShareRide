package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.car.CarDto;
import mate.academy.car_sharing_app.dto.car.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.car.UpdateCarRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CarService {
    CarDto createCar(CreateCarRequestDto createCarRequestDto);

    CarDto getCarById(Long id);

    List<CarDto> getAll(Pageable pageable);

    CarDto update(Long id, UpdateCarRequestDto updateCarRequestDto);

    void deleteById(Long id);
}
