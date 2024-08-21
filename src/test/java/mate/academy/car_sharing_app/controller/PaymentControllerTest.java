package mate.academy.car_sharing_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.car_sharing_app.dto.PaymentDto;
import mate.academy.car_sharing_app.dto.PaymentRequestDto;
import mate.academy.car_sharing_app.model.Payment;
import mate.academy.car_sharing_app.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long RENTAL_ID = 1L;
    private static final String SESSION_ID = "session1";
    private static final String SESSION_URL = "http://example.com/session1";
    private static final BigDecimal AMOUNT_TO_PAY = BigDecimal.valueOf(100.00);

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/add-3-cars-to-cars_table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/rentals/create-rentals-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/rentals/add-rental-to-rentals_table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/payments/create-payments-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/payments/add-payments-to-payments_table.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            try {
                ScriptUtils.executeSqlScript(
                        connection,
                        new ClassPathResource("database/payments/delete-payments" +
                                "-from-payments_table.sql")
                );
            } catch (ScriptStatementFailedException e) {
            }
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/delete-cars-from-cars_table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/payments/drop-payments-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/rentals/delete-rentals-from-rentals_table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/rentals/drop-rentals-table.sql")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in afterAll teardown", e);
        }
    }

    @Test
    @WithMockUser(username = "dirk@example.com", authorities = {"USER"})
    @DisplayName("Create a new payment session")
    void createPaymentSession_ValidRequest_CreatesNewPaymentSession() throws Exception {
        // Given
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(RENTAL_ID, Payment.Type.PAYMENT);

        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/api/payments")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseContent = result.getResponse().getContentAsString();
        PaymentDto actual = objectMapper.readValue(responseContent, PaymentDto.class);

        assertNotNull(actual.sessionId(), "Session ID should not be null");
        assertFalse(actual.sessionId().isEmpty(), "Session ID should not be empty");
        assertNotNull(actual.sessionUrl(), "Session URL should not be null");
        assertFalse(actual.sessionUrl().isEmpty(), "Session URL should not be empty");
    }

    @Test
    @WithMockUser(username = "dirk@example.com", authorities = {"USER"})
    @DisplayName("Get payments by user ID")
    void getPayments_ValidUserId_ReturnsPayments() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/api/payments/{id}", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<PaymentDto> actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), objectMapper.getTypeFactory()
                .constructCollectionType(List.class, PaymentDto.class));
        assertFalse(actual.isEmpty()); // Ensure payments are returned
    }

    @Test
    @WithMockUser(username = "dirk@example.com", authorities = {"USER"})
    @DisplayName("Handle payment success")
    void handlePaymentSuccess_ValidSessionId_ReturnsSuccessMessage() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/api/payments/success")
                                .param("sessionId", SESSION_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String response = result.getResponse().getContentAsString();
        Assertions.assertEquals("Payment was successful: " + SESSION_ID, response);
    }
}
