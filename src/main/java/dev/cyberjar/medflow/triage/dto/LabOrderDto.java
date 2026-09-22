package dev.cyberjar.medflow.triage.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record LabOrderDto(
        Long labOrderId,
        String testCode,
        String labFacilityName,
        OffsetDateTime orderedAt,
        List<LabResultDto> results
) {
}
