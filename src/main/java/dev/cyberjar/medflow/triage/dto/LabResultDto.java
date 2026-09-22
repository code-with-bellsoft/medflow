package dev.cyberjar.medflow.triage.dto;

import java.time.OffsetDateTime;

public record LabResultDto(
        String status,
        OffsetDateTime publishedAt
) { }
