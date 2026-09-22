package dev.cyberjar.medflow.staff;

import dev.cyberjar.medflow.staff.dto.StaffResponse;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static dev.cyberjar.medflow.Tables.FACILITY;
import static dev.cyberjar.medflow.Tables.STAFF;

@Repository
public class StaffRepository {


    private final DSLContext db;

    public StaffRepository(DSLContext db) {
        this.db = db;
    }

    public List<StaffResponse> findStaffBySpecialtyId(Long specialtyId) {

        return db.select(
                        STAFF.ID,
                        STAFF.HANDLE,
                        STAFF.ACTIVE,
                        STAFF.specialty().NAME,
                        STAFF.facility().NAME,
                        STAFF.facility().TYPE,
                        STAFF.facility().district().NAME
                )
                .from(STAFF)
                .where(STAFF.SPECIALTY_ID.eq(specialtyId))
                .and(STAFF.ACTIVE.eq(true))
                .orderBy(STAFF.facility().NAME.asc())
                .fetch(staffRecord -> new StaffResponse(
                        staffRecord.get(STAFF.ID),
                        staffRecord.get(STAFF.HANDLE),
                        staffRecord.get(STAFF.ACTIVE),
                        staffRecord.get(STAFF.specialty().NAME),
                        staffRecord.get(STAFF.facility().NAME),
                        staffRecord.get(FACILITY.TYPE).toString(),
                        staffRecord.get(STAFF.facility().district().NAME)
                ));

    }
}
