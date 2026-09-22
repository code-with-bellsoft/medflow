package dev.cyberjar.medflow.exception;

import java.net.URI;

public enum ProblemType {

    VALIDATION_FAILED("urn:neoncare:problems:validation-failed"),
    CONSTRAINT_VIOLATION("urn:neoncare:problems:constraint-violation"),
    NOT_FOUND("urn:neoncare:problems:not-found"),
    INTERNAL_ERROR("urn:neoncare:problems:internal-error"),
    BAD_JSON("urn:neoncare:problems:bad-json");


    final URI uri;

    ProblemType(String uri) {
        this.uri = URI.create(uri);
    }

}
