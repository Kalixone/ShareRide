package mate.academy.car_sharing_app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final String PATTERN_OF_EMAIL = "^[a-zA-Z0-9_+&*-]"
            + "+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]"
            + "+(?:\\.[a-zA-Z0-9-]+)*(?:\\.[a-zA-Z]{2,})$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return email != null && Pattern.compile(PATTERN_OF_EMAIL).matcher(email).matches();
    }
}
