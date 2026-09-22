package dev.cyberjar.medflow.slot;


import dev.cyberjar.medflow.slot.validation.district.ExistingDistrict;
import dev.cyberjar.medflow.slot.validation.facility.ExistingFacility;
import dev.cyberjar.medflow.slot.validation.specialty.ExistingSpecialty;
import jakarta.validation.constraints.NotNull;

public record SlotFilter(@ExistingFacility Long facilityId,
                         @ExistingSpecialty @NotNull Long specialtyId,
                         @ExistingDistrict Long districtId
) {
}
