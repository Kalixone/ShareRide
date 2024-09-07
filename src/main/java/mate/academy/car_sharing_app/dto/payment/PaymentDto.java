package mate.academy.car_sharing_app.dto.payment;

import jakarta.persistence.Column;

public record PaymentDto(
        @Column( length = 100000 )
        String sessionUrl,
        String sessionId
) {
}
