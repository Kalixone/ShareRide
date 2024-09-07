package mate.academy.car_sharing_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mate.academy.car_sharing_app.dto.rental.RentalSetActualReturnDateRequestDto;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.rental.RentalDto;
import mate.academy.car_sharing_app.dto.rental.RentalRequestDto;
import mate.academy.car_sharing_app.model.User;
import mate.academy.car_sharing_app.service.RentalService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Tag(name = "Rental management", description = "Endpoints for managing car rentals")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @Operation(
            summary = "Create a new rental",
            description = "Create a new rental record for a car." +
                    " Requires user authentication to link the rental to the current user."
    )
    RentalDto rentACar(Authentication authentication,
                       @RequestBody RentalRequestDto rentalRequestDto) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        return rentalService.rentACar(userId, rentalRequestDto);
    }

    @GetMapping
    @Operation(
            summary = "Get all active rentals for the authenticated user",
            description = "Retrieve a list of all active car rentals" +
                    " for the currently authenticated user."
    )
    List<RentalDto> getRentalsByUserId(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        return rentalService.getActiveRentalsByUserId(userId);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a specific rental by ID for the authenticated user",
            description = "Retrieve detailed information about a specific rental by ID." +
                    " The rental must belong to the currently authenticated user."
    )
    RentalDto getSpecificRentalByUserId(Authentication authentication, @PathVariable Long id) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        return rentalService.getSpecificRentalByUserId(userId, id);
    }

    @PostMapping("/return")
    @Operation(
            summary = "Set the actual return date for a rental",
            description = "Update the rental record to set the actual return date for the car." +
                    " This is used when a car is returned."
    )
    RentalDto setActualReturnDate(@RequestBody RentalSetActualReturnDateRequestDto
                                          rentalSetActualReturnDateRequestDto) {
        return rentalService.setActualReturnDate(rentalSetActualReturnDateRequestDto);
    }
}
