package dev.cyberjar.medflow.slot.validation.specialty;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExistingSpecialtyValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingSpecialty {

    String message() default "Specialty with this id doesn't exist.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
