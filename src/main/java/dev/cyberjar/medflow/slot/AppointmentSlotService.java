package dev.cyberjar.medflow.slot;


import dev.cyberjar.medflow.exception.NotFoundException;
import dev.cyberjar.medflow.slot.dto.SlotRequest;
import dev.cyberjar.medflow.slot.dto.SlotResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentSlotService {

    private final AppointmentSlotRepository repository;

    public AppointmentSlotService(AppointmentSlotRepository appointmentSlotRepository) {
        this.repository = appointmentSlotRepository;
    }

    public List<SlotResponse> findSlots(SlotFilter filter) {
        return repository.findSlots(filter);
    }

    public SlotResponse findSlotById(Long id) {
        Optional<SlotResponse> response = repository.findSlotById(id);
        if (response.isEmpty()) {
            throw new NotFoundException("Slot with id " + id + " not found");
        }
        return response.get();
    }

    public Long createSlot(SlotRequest slot) {

        return repository.createSlot(slot);
    }

    public void updateSlot(Long slotId, int newCapacity) {
        repository.updateSlot(slotId, newCapacity);
    }

    public void deleteSlot(Long slotId) {
        repository.deleteSlot(slotId);
    }

}
