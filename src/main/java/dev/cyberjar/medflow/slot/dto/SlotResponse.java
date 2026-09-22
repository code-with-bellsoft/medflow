package dev.cyberjar.medflow.slot.dto;

import java.time.OffsetDateTime;

public record SlotResponse(
        Long id,
        Long facilityId,
        String facilityName,
        Long specialtyId,
        String specialtyName,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        int capacity
) {
}
