package dev.cyberjar.medflow.slot.validation.facility;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.cyberjar.medflow.Tables.FACILITY;

public class ExistingFacilityValidator implements ConstraintValidator<ExistingFacility, Long> {

    private DSLContext db;

    @Autowired
    public void setDb(DSLContext repository) {
        this.db = repository;
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (id == null) return true;
        return db.fetchExists(
                db.selectFrom(FACILITY).where(FACILITY.ID.eq(id))
        );
    }
}