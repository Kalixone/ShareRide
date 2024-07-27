package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.CarDto;
import mate.academy.car_sharing_app.dto.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.UpdateCarRequestDto;

import java.util.List;

public interface CarService {
    CarDto createCar(CreateCarRequestDto createCarRequestDto);

    CarDto getCarById(Long id);

    List<CarDto> getAll();

    CarDto update(Long id, UpdateCarRequestDto updateCarRequestDto);

    void deleteById(Long id);
}
