package mate.academy.car_sharing_app.dto.car;

import jakarta.validation.constraints.NotNull;
import mate.academy.car_sharing_app.model.Car;
import org.hibernate.validator.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        @NotBlank
        String model,
        @NotBlank
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
