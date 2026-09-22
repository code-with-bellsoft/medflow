package dev.cyberjar.medflow.slot.dto;

import dev.cyberjar.medflow.slot.validation.facility.ExistingFacility;
import dev.cyberjar.medflow.slot.validation.specialty.ExistingSpecialty;
import dev.cyberjar.medflow.slot.validation.time.EndAfterStart;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record SlotRequest(
        @NotNull @ExistingFacility Long facilityId,
        @NotNull @ExistingSpecialty Long specialtyId,
        @NotNull @Future(message = "Slot should start in the future.") OffsetDateTime startsAt,
        @NotNull @Future(message = "Slot should end in the future.") @EndAfterStart OffsetDateTime endsAt,
        @NotNull @Positive(message = "Slot capacity should be greater than zero.") int capacity) {
}
