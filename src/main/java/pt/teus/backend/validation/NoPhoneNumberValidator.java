package pt.teus.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NoPhoneNumberValidator implements ConstraintValidator<NoPhoneNumber, String> {
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("\\b\\d{10}\\b|\\b(?:\\d{3}[-.\\s]?){2}\\d{4}\\b");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Allow null or empty description
        }
        return !PHONE_PATTERN.matcher(value).find();
    }
}
