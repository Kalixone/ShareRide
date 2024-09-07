package mate.academy.car_sharing_app.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mate.academy.car_sharing_app.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositoryTest {
    private static final Long CAR_ID = 1L;

    @Autowired
    private CarRepository carRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("Verify decreaseInventory() method works")
    @Sql(scripts = {
            "classpath:database/cars/delete-cars-from-cars_table.sql",
            "classpath:database/cars/add-3-cars-to-cars_table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cars/delete-cars-from-cars_table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void decreaseInventory_ValidCarId_DecreasesInventory() {
        Car car = carRepository.findById(CAR_ID).orElseThrow();
        int inventory = car.getInventory();

        carRepository.decreaseInventory(CAR_ID);
        entityManager.flush();
        entityManager.refresh(car);

        Car updatedCar = carRepository.findById(CAR_ID).orElseThrow();
        assertThat(updatedCar.getInventory()).isEqualTo(inventory - 1);
    }

    @Test
    @DisplayName("Verify increaseInventory() method works")
    @Sql(scripts = {
            "classpath:database/cars/delete-cars-from-cars_table.sql",
            "classpath:database/cars/add-3-cars-to-cars_table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cars/delete-cars-from-cars_table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void increaseInventory_ValidCarId_IncreaseInventory() {
        Car car = carRepository.findById(CAR_ID).orElseThrow();
        int inventory = car.getInventory();

        carRepository.increaseInventory(CAR_ID);
        entityManager.flush();
        entityManager.refresh(car);

        Car updatedCar = carRepository.findById(CAR_ID).orElseThrow();
        assertThat(updatedCar.getInventory()).isEqualTo(inventory + 1);
    }
}
