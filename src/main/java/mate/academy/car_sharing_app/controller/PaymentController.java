package mate.academy.car_sharing_app.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.PaymentDto;
import mate.academy.car_sharing_app.dto.PaymentRequestDto;
import mate.academy.car_sharing_app.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPaymentSession(@RequestBody PaymentRequestDto paymentRequestDto) {
        return paymentService.createPaymentSession(paymentRequestDto);
    }

    @GetMapping("/{id}")
    public List<PaymentDto> getPayments(@PathVariable Long id) {
        return paymentService.getPaymentsByUserId(id);
    }

    @GetMapping("/success")
    public String handlePaymentSuccess(@RequestParam String sessionId) {
        return paymentService.handlePaymentSuccess(sessionId);
    }

    @GetMapping("/cancel")
    public String handlePaymentCancel() {
        return paymentService.handlePaymentCancel();
    }
}
