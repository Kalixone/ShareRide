package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.payment.PaymentDto;
import mate.academy.car_sharing_app.model.Payment;
import mate.academy.car_sharing_app.repository.PaymentRepository;
import mate.academy.car_sharing_app.repository.RentalRepository;
import mate.academy.car_sharing_app.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    private static final Long RENTAL_ID = 1L;
    private static final String SESSION_URL = "http://stripe.com/session";
    private static final String SESSION_ID = "cs_test_sessionId";
    private static final Long USER_ID = 1L;
    private static final String CANCELLED = "Payment was cancelled!";

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Verify getPaymentsByUserId() method works")
    public void getPaymentsByUserId_ValidUserId_ReturnsPaymentList() {
        List<Payment> payments = createTestPayments();
        List<PaymentDto> paymentDtos = payments.stream()
                .map(this::createTestPaymentDto)
                .collect(Collectors.toList());

        when(rentalRepository.findRentalIdsByUserId(USER_ID)).thenReturn(Arrays.asList(RENTAL_ID));
        when(paymentRepository.findByRentalId(RENTAL_ID)).thenReturn(payments);

        List<PaymentDto> result = paymentService.getPaymentsByUserId(USER_ID);

        assertThat(result).isEqualTo(paymentDtos);
    }

    @Test
    @DisplayName("Verify handlePaymentSuccess() method works")
    public void handlePaymentSuccess_ValidSessionId_UpdatesPaymentStatus() {
        Payment payment = createTestPayment();
        payment.setStatus(Payment.Status.PENDING);

        when(paymentRepository.findBySessionId(SESSION_ID)).thenReturn(Optional.of(payment));

        String result = paymentService.handlePaymentSuccess(SESSION_ID);

        assertThat(result).isEqualTo("Payment was successful: " + SESSION_ID);
        assertThat(payment.getStatus()).isEqualTo(Payment.Status.PAID);
    }

    @Test
    @DisplayName("Verify handlePaymentCancel() method works")
    public void handlePaymentCancel_ReturnsCancelMessage() {
        String result = paymentService.handlePaymentCancel();

        assertThat(result).isEqualTo(CANCELLED);
    }

    private Payment createTestPayment() {
        Payment payment = new Payment();
        payment.setSessionUrl(SESSION_URL);
        payment.setSessionId(SESSION_ID);
        return payment;
    }

    private PaymentDto createTestPaymentDto(Payment payment) {
        return new PaymentDto(payment.getSessionUrl(), payment.getSessionId());
    }

    private List<Payment> createTestPayments() {
        Payment payment1 = new Payment();
        payment1.setSessionUrl(SESSION_URL);
        payment1.setSessionId(SESSION_ID + "1");

        Payment payment2 = new Payment();
        payment2.setSessionUrl(SESSION_URL);
        payment2.setSessionId(SESSION_ID + "2");

        return Arrays.asList(payment1, payment2);
    }
}
