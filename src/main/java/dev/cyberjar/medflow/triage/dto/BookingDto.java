package dev.cyberjar.medflow.triage.dto;

import java.time.OffsetDateTime;

public record BookingDto(
        Long bookingId,
        String status,
        OffsetDateTime createdAt,
        String facilityName,
        OffsetDateTime slotStartsAt,
        String staffHandle
) { }
