package dev.cyberjar.medflow.staff;


import dev.cyberjar.medflow.staff.dto.StaffResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@Validated
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/{specialtyId}")
    @ResponseStatus(HttpStatus.OK)
    public List<StaffResponse> getAllStuffBySpecialtyId(
            @NotNull
            @PathVariable
            Long specialtyId) {
        return staffService.findStaffBySpecialtyId(specialtyId);
    }

}
