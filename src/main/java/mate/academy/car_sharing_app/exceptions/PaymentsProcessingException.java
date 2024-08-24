package mate.academy.car_sharing_app.exceptions;

public class PaymentsProcessingException extends RuntimeException {
    public PaymentsProcessingException(String message) {
        super(message);
    }
}
