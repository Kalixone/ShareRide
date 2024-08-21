package mate.academy.car_sharing_app.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.PaymentDto;
import mate.academy.car_sharing_app.dto.PaymentRequestDto;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.model.Payment;
import mate.academy.car_sharing_app.model.Rental;
import mate.academy.car_sharing_app.repository.CarRepository;
import mate.academy.car_sharing_app.repository.PaymentRepository;
import mate.academy.car_sharing_app.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;

    private static final String stripeApiKey = "sk_test_51PjGjHDV8VpfWm572fx35t8yJywf1SGPAuLqw" +
            "Ce1bWDTCwto9PW2LIyon7nbCjGNJMdaTt124axrlkvjSkES8gMB00kmDvtXpx";

    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(2);

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentDto createPaymentSession(PaymentRequestDto paymentRequestDto) {
        Long rentalId = paymentRequestDto.rentalId();
        Payment.Type paymentType = paymentRequestDto.paymentType();

        // Obliczanie kwoty do zapłaty
        BigDecimal amountToPay = calculateAmountToPay(rentalId, paymentType.toString());

        // Walidacja kwoty
        if (amountToPay.signum() <= 0) {
            throw new IllegalArgumentException("Amount to pay must be positive and non-zero.");
        }

        // Konwersja kwoty na grosze
        long amountInCents = amountToPay.multiply(BigDecimal.valueOf(100)).longValueExact();

        Map<String, String> successParams = Map.of("sessionId", "{CHECKOUT_SESSION_ID}");
        Map<String, String> cancelParams = Map.of("cancelParam", "cancelValue");

        // Tworzenie sesji Stripe
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amountInCents) // Kwota w groszach
                                .setProductData(SessionCreateParams.LineItem
                                        .PriceData.ProductData.builder()
                                        .setName("Rental Payment")
                                        .build())
                                .build())
                        .setQuantity(1L) // Ilość 1, bo wynajmujemy tylko jeden samochód
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/payments/success?sessionId={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .build();

        try {
            Session session = Session.create(params);

            // Zapisanie sesji płatności w bazie
            Payment payment = new Payment();
            payment.setSessionUrl(session.getUrl());
            payment.setSessionId(session.getId());
            payment.setAmountToPay(amountToPay);
            payment.setRentalId(rentalId);
            payment.setStatus(Payment.Status.PENDING);
            payment.setType(paymentType);

            paymentRepository.save(payment);

            return new PaymentDto(session.getUrl(), session.getId());
        } catch (StripeException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating payment session", e);
        }
    }

    @Override
    public BigDecimal calculateAmountToPay(Long rentalId, String paymentType) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID"));

        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid car ID"));

        if ("FINE".equalsIgnoreCase(paymentType)) {
            LocalDate returnDate = rental.getReturnDate();
            LocalDate actualReturnDate = rental.getActualReturnDate();

            if (actualReturnDate != null && returnDate != null
                    && actualReturnDate.isAfter(returnDate)) {
                long overdueDays = ChronoUnit.DAYS
                        .between(returnDate, actualReturnDate);
                return car.getDailyFee()
                        .multiply(BigDecimal.valueOf(overdueDays)).multiply(FINE_MULTIPLIER);
            }
            return BigDecimal.ZERO; // No fine if not overdue
        } else {
            LocalDate rentalDate = rental.getRentalDate();
            LocalDate actualReturnDate = rental.getActualReturnDate();

            if (actualReturnDate == null) {
                throw new IllegalStateException("Actual return date" +
                        " must be set for rental charges.");
            }

            long rentalDays = ChronoUnit.DAYS.between(rentalDate, actualReturnDate);
            if (rentalDays < 0) {
                return BigDecimal.ZERO; // No charge if rental period is not yet finished
            }
            return car.getDailyFee().multiply(BigDecimal.valueOf(rentalDays));
        }
    }

    private String buildUrl(String path, Map<String, String> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080")
                .path(path);

        // Dodaj parametry zapytania do URL
        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }

        return uriBuilder.toUriString();
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        // Find rental IDs for the given user ID
        List<Long> rentalIds = rentalRepository.findRentalIdsByUserId(userId);

        // 2. For each rental ID, find payments and collect them into a list
        List<PaymentDto> payments = rentalIds.stream()
                .flatMap(rentalId -> paymentRepository.findByRentalId(rentalId).stream())
                .map(payment -> new PaymentDto(payment.getSessionUrl(), payment.getSessionId()))
                .collect(Collectors.toList());

        return payments;
    }

    @Override
    public String handlePaymentSuccess(String sessionId) {
        // Pobierz płatność z repozytorium
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session ID"));

        // Zaktualizuj status płatności
        payment.setStatus(Payment.Status.PAID);
        paymentRepository.save(payment);

        return "Payment was successful: " + sessionId;
    }

    @Override
    public String handlePaymentCancel() {
        return "Payment was cancelled!";
    }
}
