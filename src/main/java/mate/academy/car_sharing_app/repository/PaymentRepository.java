package mate.academy.car_sharing_app.repository;

import mate.academy.car_sharing_app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRentalId(Long rentalId);

    Optional<Payment> findBySessionId(String sessionId);
}
