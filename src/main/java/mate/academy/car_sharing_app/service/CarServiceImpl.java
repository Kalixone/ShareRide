package mate.academy.car_sharing_app.service;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.CarDto;
import mate.academy.car_sharing_app.dto.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.UpdateCarRequestDto;
import mate.academy.car_sharing_app.exceptions.EntityNotFoundException;
import mate.academy.car_sharing_app.mapper.CarMapper;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.repository.CarRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarMapper carMapper;
    private final CarRepository carRepository;

    @Override
    public CarDto createCar(CreateCarRequestDto createCarRequestDto) {
        Car car = carMapper.toModel(createCarRequestDto);
        Car savedCar = carRepository.save(car);
        return carMapper.toDto(savedCar);
    }

    @Override
    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id));
        return carMapper.toDto(car);
    }

    @Override
    public List<CarDto> getAll() {
        List<Car> allCars = carRepository.findAll();
        return allCars.stream().map(carMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CarDto update(Long id, UpdateCarRequestDto updateCarRequestDto) {
        Car existingCar = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id));
        Car car = carMapper.updateModel(updateCarRequestDto, existingCar);
        Car updatedCar = carRepository.save(car);
        return carMapper.toDto(updatedCar);
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}
