package mate.academy.car_sharing_app.repository;

import mate.academy.car_sharing_app.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserIdAndActualReturnDateIsNull(Long userId);

    @Query("SELECT r FROM Rental r WHERE r.returnDate <= :today AND r.actualReturnDate IS NULL")
    List<Rental> findOverdueRentals(@Param("today") LocalDate today);

    @Query("SELECT r.id FROM Rental r WHERE r.userId = :userId")
    List<Long> findRentalIdsByUserId(@Param("userId") Long userId);
}
