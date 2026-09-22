package dev.cyberjar.medflow.slot.validation.time;

import dev.cyberjar.medflow.slot.dto.SlotRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, SlotRequest> {

    @Override
    public boolean isValid(SlotRequest request, ConstraintValidatorContext constraintValidatorContext) {

        if (request == null) return true;
        return request.endsAt().isAfter(request.startsAt());
    }

}
