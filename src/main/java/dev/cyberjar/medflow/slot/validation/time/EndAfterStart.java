package dev.cyberjar.medflow.slot.validation.time;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EndAfterStartValidator.class)
@Target( { ElementType.PARAMETER, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface EndAfterStart {

    String message() default "Slot end time must be later than start time.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
