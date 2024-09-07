package mate.academy.car_sharing_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.car.CarDto;
import mate.academy.car_sharing_app.dto.car.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.car.UpdateCarRequestDto;
import mate.academy.car_sharing_app.service.CarService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Tag(name = "Car management", description = "Endpoints for managing car inventory")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(
            summary = "Create a new car record",
            description = "Create a new record for a car in the inventory." +
                    " The request body must include details like make, model, and year of the car."
    )
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto createCarRequestDto) {
       return carService.createCar(createCarRequestDto);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve car details by ID",
            description = "Retrieve detailed information about a car using its ID." +
                    " This includes details such as make, model, year, and availability."
    )
    public CarDto getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping
    @Operation(
            summary = "Get a list of all cars",
            description = "Retrieve a list of all cars available in the inventory." +
                    " This list includes basic details for each car."
    )
    public List<CarDto> getAll(Pageable pageable) {
        return carService.getAll(pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(
            summary = "Update car details by ID",
            description = "Update the details of an existing car identified by its ID." +
                    " The request body must include updated car information" +
                    " such as make, model, and year."
    )
    public CarDto update(@PathVariable Long id,
                  @RequestBody @Valid
                  UpdateCarRequestDto updateCarRequestDto) {
        return carService.update(id, updateCarRequestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(
            summary = "Delete a car record by ID",
            description = "Delete a car record from the inventory using its ID." +
                    " This operation removes the car from the system."
    )
    public void deleteById(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
