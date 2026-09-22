package dev.cyberjar.medflow.slot.validation.specialty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.cyberjar.medflow.Tables.SPECIALTY;

public class ExistingSpecialtyValidator implements ConstraintValidator<ExistingSpecialty, Long> {

    private DSLContext db;

    @Autowired
    public void setDb(DSLContext repository) {
        this.db = repository;
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (id == null) return true;
        return db.fetchExists(
                db.selectFrom(SPECIALTY).where(SPECIALTY.ID.eq(id))
        );
    }

}
