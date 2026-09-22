package dev.cyberjar.medflow.slot;


import dev.cyberjar.medflow.slot.dto.SlotRequest;
import dev.cyberjar.medflow.slot.dto.SlotResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slot")
@Validated
public class AppointmentSlotController {

    private final AppointmentSlotService appointmentSlotService;

    public AppointmentSlotController(AppointmentSlotService appointmentSlotService) {
        this.appointmentSlotService = appointmentSlotService;
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<SlotResponse> getAllSlots(
            @NotNull
            @RequestBody
            @Valid
            SlotFilter filter) {
        return appointmentSlotService.findSlots(filter);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SlotResponse getSlotById(
            @NotNull
            @PathVariable
            Long id) {
        return appointmentSlotService.findSlotById(id);

    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createSlot(
            @NotNull
            @RequestBody
            @Valid
            SlotRequest slotDto) {
        return appointmentSlotService.createSlot(slotDto);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateSlot(
            @NotNull
            @PathVariable
            Long id,
            @NotNull
            @RequestBody
            @Valid
            @Positive(message = "Slot capacity should be greater than zero.")
            int newCapacity) {
        appointmentSlotService.updateSlot(id, newCapacity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSlot(
            @NotNull
            @PathVariable Long id) {
        appointmentSlotService.deleteSlot(id);
    }

}
