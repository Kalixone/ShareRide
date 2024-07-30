package mate.academy.car_sharing_app.repository;

import mate.academy.car_sharing_app.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    @Modifying
    @Query("UPDATE Car c SET c.inventory = c.inventory - 1 WHERE c.id = :carId AND c.inventory > 0")
    void decreaseInventory(Long carId);

    @Modifying
    @Query("UPDATE Car c SET c.inventory = c.inventory + 1 WHERE c.id = :carId")
    void increaseInventory(Long carId);
}
