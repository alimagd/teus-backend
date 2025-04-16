package pt.teus.backend.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoPhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoPhoneNumber {
    String message() default "Description must not contain a phone or cell number.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

