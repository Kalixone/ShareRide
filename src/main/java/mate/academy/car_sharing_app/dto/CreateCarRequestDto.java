package mate.academy.car_sharing_app.dto;

import mate.academy.car_sharing_app.model.Car;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        String model,
        String brand,
        Car.TypeCar type,
        int inventory,
        BigDecimal dailyFee
) {
}
