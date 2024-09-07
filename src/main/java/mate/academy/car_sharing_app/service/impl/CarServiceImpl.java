package mate.academy.car_sharing_app.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.car.CarDto;
import mate.academy.car_sharing_app.dto.car.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.car.UpdateCarRequestDto;
import mate.academy.car_sharing_app.exceptions.EntityNotFoundException;
import mate.academy.car_sharing_app.mapper.CarMapper;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.repository.CarRepository;
import mate.academy.car_sharing_app.service.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarMapper carMapper;
    private final CarRepository carRepository;

    @Override
    public CarDto createCar(CreateCarRequestDto createCarRequestDto) {
        Car car = carMapper.toModel(createCarRequestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id));
        return carMapper.toDto(car);
    }

    @Override
    public List<CarDto> getAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto update(Long id, UpdateCarRequestDto updateCarRequestDto) {
        Car existingCar = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id));
        return carMapper.toDto(carRepository
                .save(carMapper.updateModel(updateCarRequestDto, existingCar)));
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}
