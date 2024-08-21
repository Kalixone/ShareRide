package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.PaymentDto;
import mate.academy.car_sharing_app.dto.PaymentRequestDto;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    PaymentDto createPaymentSession(PaymentRequestDto paymentRequestDto);

    List<PaymentDto> getPaymentsByUserId(Long userId);

    String handlePaymentSuccess(String sessionId);

    String handlePaymentCancel();

    BigDecimal calculateAmountToPay(Long rentalId, String paymentType);
}
