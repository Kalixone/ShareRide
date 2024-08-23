package mate.academy.car_sharing_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.PaymentDto;
import mate.academy.car_sharing_app.dto.PaymentRequestDto;
import mate.academy.car_sharing_app.service.PaymentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(
            summary = "Create a new payment session",
            description = "Initiate a new payment session with the given payment details. " +
                    "The session will be used to process the payment."
    )
    public PaymentDto createPaymentSession(@RequestBody PaymentRequestDto paymentRequestDto) {
        return paymentService.createPaymentSession(paymentRequestDto);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get payments by user ID",
            description = "Retrieve a list of all payments associated with a specific user ID."
    )
    public List<PaymentDto> getPayments(@PathVariable Long id) {
        return paymentService.getPaymentsByUserId(id);
    }

    @GetMapping("/success")
    @Operation(
            summary = "Handle payment success",
            description = "Handle the success of a payment session, typically triggered by " +
                    "a callback from the payment gateway after the payment" +
                    " is successfully completed."
    )
    public String handlePaymentSuccess(@RequestParam String sessionId) {
        return paymentService.handlePaymentSuccess(sessionId);
    }

    @GetMapping("/cancel")
    @Operation(
            summary = "Handle payment cancellation",
            description = "Handle the cancellation of a payment session." +
                    " This endpoint is triggered " +
                    "when a user cancels the payment process."
    )
    public String handlePaymentCancel() {
        return paymentService.handlePaymentCancel();
    }
}
