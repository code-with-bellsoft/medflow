package dev.cyberjar.medflow.triage;

import dev.cyberjar.medflow.triage.dto.TriageCaseDetailsResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/triage")
@Validated
public class TriageController {

    private final TriageService triageService;

    public TriageController(TriageService triageService) {
        this.triageService = triageService;
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TriageCaseDetailsResponse getTriageCaseById(
            @NotNull
            @PathVariable
            Long id) {
        return triageService.findTriageCase(id);

    }
}
