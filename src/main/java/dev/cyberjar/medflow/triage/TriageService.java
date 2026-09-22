package dev.cyberjar.medflow.triage;

import dev.cyberjar.medflow.exception.NotFoundException;
import dev.cyberjar.medflow.triage.dto.TriageCaseDetailsResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TriageService {

    private final TriageRepository triageRepository;

    public TriageService(TriageRepository triageRepository) {
        this.triageRepository = triageRepository;
    }

    public TriageCaseDetailsResponse findTriageCase(Long triageCaseId) {
        Optional<TriageCaseDetailsResponse> response = triageRepository.findTriageCase(triageCaseId);
        if (response.isEmpty()) {
            throw new NotFoundException("Triage case with id " + triageCaseId + " not found");
        }
        return response.get();
    }

}
