package dev.cyberjar.medflow.scheduling;

import dev.cyberjar.medflow.scheduling.dto.SlotSuggestionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulingService {

    private final SchedulingRepository schedulingRepository;

    public SchedulingService(SchedulingRepository schedulingRepository) {
        this.schedulingRepository = schedulingRepository;
    }

    public List<SlotSuggestionResponse> suggestSlotsForTriageCase(Long triageCaseId) {
        return schedulingRepository.suggestSlots(triageCaseId);

    }

}
