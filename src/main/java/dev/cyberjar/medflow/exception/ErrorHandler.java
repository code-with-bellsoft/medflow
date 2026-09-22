package dev.cyberjar.medflow.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            NotFoundException ex,
            WebRequest request) {

        String errorId = UUID.randomUUID().toString();

        log.warn("errorId={} uri={} msg={}",
                errorId, extractPath(request), ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problemDetail.setDetail("The requested resource was not found.");
        problemDetail.setInstance(extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        problemDetail.setProperty("errorCategory", "CLIENT_ERROR");
        problemDetail.setType(ProblemType.NOT_FOUND.uri);


        return problemResponse(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String errorId = UUID.randomUUID().toString();

        log.warn("errorId={} uri={} validationErrors={}",
                errorId, extractPath(request), ex.getBindingResult().getErrorCount());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setDetail("One or more fields are invalid.");
        problemDetail.setInstance(extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        problemDetail.setProperty("errorCategory", "CLIENT_ERROR");

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> {
                    return Map.of(
                            "field", err.getField(),
                            "message", err.getDefaultMessage() == null ? "Invalid value" : err.getDefaultMessage()
                    );
                })
                .toList();

        problemDetail.setProperty("errors", errors);
        problemDetail.setType(ProblemType.VALIDATION_FAILED.uri);

        return problemResponse(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintValidationExceptions(
            ConstraintViolationException ex,
            WebRequest request) {

        String errorId = UUID.randomUUID().toString();

        log.warn("errorId={} uri={} constraintErrors={}",
                errorId, extractPath(request), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setDetail("One or more request parameters are invalid.");
        problemDetail.setInstance(extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        problemDetail.setProperty("errorCategory", "CLIENT_ERROR");

        List<Map<String, String>> violations = ex.getConstraintViolations()
                .stream()
                .map(v -> Map.of(
                        "path", v.getPropertyPath().toString(),
                        "message", v.getMessage()
                ))
                .toList();

        problemDetail.setProperty("violations", violations);
        problemDetail.setType(ProblemType.CONSTRAINT_VIOLATION.uri);

        return problemResponse(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        String errorId = UUID.randomUUID().toString();

        log.warn("errorId={} uri={} badJson={}", errorId, extractPath(request), ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail("Request body could not be parsed or mapped to the expected schema.");
        problemDetail.setInstance(extractPath(request));
        problemDetail.setProperty("errorId", errorId);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorCategory", "CLIENT_ERROR");
        problemDetail.setType(ProblemType.BAD_JSON.uri);

        return problemResponse(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandled(Exception ex, WebRequest request) {
        String errorId = UUID.randomUUID().toString();

        log.error("errorId={} uri={} msg={}",
                errorId, extractPath(request), ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        problemDetail.setDetail("Something went wrong. If the problem persists, contact support with errorId=" + errorId + ".");
        problemDetail.setInstance(extractPath(request));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errorId", errorId);
        problemDetail.setProperty("errorCategory", "SERVER_ERROR");
        problemDetail.setType(ProblemType.INTERNAL_ERROR.uri);

        return problemResponse(problemDetail);
    }

    /*
    Helper Methods
     */

    private URI extractPath(WebRequest request) {
        String desc = request.getDescription(false);
        String uriPart = desc.startsWith("uri=") ? desc.substring(4) : desc;
        return URI.create(uriPart);
    }

    private ResponseEntity<ProblemDetail> problemResponse(ProblemDetail problemDetail) {
        return ResponseEntity
                .status(problemDetail.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }

}
