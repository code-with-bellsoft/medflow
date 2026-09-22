package dev.cyberjar.medflow.triage.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record TriageCaseDetailsResponse(
        Long caseId,
        String patientPublicRef,
        OffsetDateTime createdAt,
        int severity,
        String status,
        String intakeFacilityName,
        String requiredSpecialtyName,
        List<BookingDto> bookings,
        List<LabOrderDto> labOrders
) { }
