package mate.academy.car_sharing_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.CarDto;
import mate.academy.car_sharing_app.dto.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.UpdateCarRequestDto;
import mate.academy.car_sharing_app.service.CarService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    CarDto createCar(@RequestBody @Valid CreateCarRequestDto createCarRequestDto) {
       return carService.createCar(createCarRequestDto);
    }

    @GetMapping("/{id}")
    CarDto getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping
    List<CarDto> getAll() {
        return carService.getAll();
    }

    @PutMapping("/{id}")
    CarDto update(@PathVariable Long id,
                  @RequestBody @Valid
                  UpdateCarRequestDto updateCarRequestDto) {
        return carService.update(id, updateCarRequestDto);
    }

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
