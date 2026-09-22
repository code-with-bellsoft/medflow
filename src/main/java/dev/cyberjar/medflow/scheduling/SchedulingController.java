package dev.cyberjar.medflow.scheduling;


import dev.cyberjar.medflow.scheduling.dto.SlotSuggestionResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@Validated
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @GetMapping("/{triageCaseId}")
    @ResponseStatus(HttpStatus.OK)
    public List<SlotSuggestionResponse> getSuggestedSlotsByTriageCaseId(
            @NotNull
            @PathVariable
            Long triageCaseId) {
        return schedulingService.suggestSlotsForTriageCase(triageCaseId);
    }

}
