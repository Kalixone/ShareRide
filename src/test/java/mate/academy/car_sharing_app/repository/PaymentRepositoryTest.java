package mate.academy.car_sharing_app.repository;

import mate.academy.car_sharing_app.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTest {
    private static final Long RENTAL_ID = 1L;
    private static final String SESSION_ID = "session1";

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Verify testFindByRentalId() method works")
    @Sql(scripts = {
            "classpath:database/payments/create-payments-table.sql",
            "classpath:database/payments/add-payments-to-payments_table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payments/delete-payments-from-payments_table.sql",
            "classpath:database/payments/drop-payments-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByRentalId_ShouldReturnPaymentsForGivenRentalId() {
        List<Payment> byRentalId = paymentRepository.findByRentalId(RENTAL_ID);

        assertThat(byRentalId).isNotNull();
        assertThat(byRentalId).hasSize(2);

        List<String> expectedSessionIds = List.of("session1", "session2");
        List<String> actualSessionIds = byRentalId.stream()
                .map(Payment::getSessionId)
                .sorted()
                .collect(Collectors.toList());

        assertThat(actualSessionIds).isEqualTo(expectedSessionIds);
    }

    @Test
    @DisplayName("Verify testFindByRentalId() method works")
    @Sql(scripts = {
            "classpath:database/payments/create-payments-table.sql",
            "classpath:database/payments/add-payments-to-payments_table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payments/delete-payments-from-payments_table.sql",
            "classpath:database/payments/drop-payments-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findBySessionId_ShouldReturnPaymentForGivenSessionId() {
        Optional<Payment> bySessionId = paymentRepository.findBySessionId(SESSION_ID);

        assertThat(bySessionId)
                .isPresent()
                .get()
                .extracting(Payment::getSessionId)
                .isEqualTo(SESSION_ID);
    }
}
