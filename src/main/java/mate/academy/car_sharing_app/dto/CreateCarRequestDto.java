package mate.academy.car_sharing_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import mate.academy.car_sharing_app.model.Car;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        @NotEmpty
        String model,
        @NotEmpty
        String brand,
        @NotNull
        Car.TypeCar type,
        @Min(0)
        int inventory,
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal dailyFee
) {
}
