package mate.academy.car_sharing_app.repository;

import mate.academy.car_sharing_app.model.Rental;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {
    private static final Long USER_ID = 1L;
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Verify findOverdueRentals() method works")
    @Sql(scripts = {
            "classpath:database/rentals/create-rentals-table.sql",
            "classpath:database/rentals/add-rental-to-rentals_table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-rentals-from-rentals_table.sql",
            "classpath:database/rentals/drop-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findOverdueRentals_ShouldReturnOverdueRentals() {
        LocalDate today = LocalDate.of(2024,7,11);

        List<Rental> rentals = rentalRepository.findOverdueRentals(today);

        assertThat(rentals).isNotNull();
        assertThat(rentals).allMatch(rental -> rental.getReturnDate().isBefore(today));
    }

    @Test
    @DisplayName("Verify findRentalIdsByUserId() method works")
    @Sql(scripts = {
            "classpath:database/rentals/create-rentals-table.sql",
            "classpath:database/rentals/add-rental-to-rentals_table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/delete-rentals-from-rentals_table.sql",
            "classpath:database/rentals/drop-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findRentalIdsByUserId_ShouldReturnRentalIdsForUser() {
        List<Long> rentalIdsByUserId = rentalRepository.findRentalIdsByUserId(USER_ID);

        assertThat(rentalIdsByUserId).isNotNull();
        assertThat(rentalIdsByUserId).hasSize(2);
    }
}
