package dev.cyberjar.medflow.staff;

import dev.cyberjar.medflow.staff.dto.StaffResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<StaffResponse> findStaffBySpecialtyId(Long specialtyId) {
        return staffRepository.findStaffBySpecialtyId(specialtyId);
    }

}
