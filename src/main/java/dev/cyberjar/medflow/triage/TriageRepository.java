package dev.cyberjar.medflow.triage;

import dev.cyberjar.medflow.triage.dto.BookingDto;
import dev.cyberjar.medflow.triage.dto.LabOrderDto;
import dev.cyberjar.medflow.triage.dto.LabResultDto;
import dev.cyberjar.medflow.triage.dto.TriageCaseDetailsResponse;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static dev.cyberjar.medflow.Tables.*;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Repository
public class TriageRepository {

    private final DSLContext db;

    public TriageRepository(DSLContext db) {
        this.db = db;
    }


    /*

    This method shows how to use multiset to collect the results of a non-scalar subquery
    into a single nested collection value. It:

    * Loads a single triage case and returns a REST-ready object graph in one DB round trip.

    * Uses jOOQ MULTISET to fetch nested collections: bookings (with slot facility + staff) and lab orders
    * (each with nested lab results), avoiding N+1 queries and manual grouping.

    * Returns Optional.empty() if the triage case id does not exist.


     */

    public Optional<TriageCaseDetailsResponse> findTriageCase(Long triageCaseId) {

        Field<List<BookingDto>> bookings =
                multiset(
                        select(
                                BOOKING.ID,
                                BOOKING.STATUS,
                                BOOKING.CREATED_AT,
                                FACILITY.NAME,
                                APPOINTMENT_SLOT.STARTS_AT,
                                STAFF.HANDLE
                        )
                                .from(BOOKING)
                                .join(APPOINTMENT_SLOT).on(APPOINTMENT_SLOT.ID.eq(BOOKING.APPOINTMENT_SLOT_ID))
                                .join(FACILITY).on(FACILITY.ID.eq(APPOINTMENT_SLOT.FACILITY_ID))
                                .leftJoin(STAFF).on(STAFF.ID.eq(BOOKING.STAFF_ID))
                                .where(BOOKING.TRIAGE_CASE_ID.eq(TRIAGE_CASE.ID))
                                .orderBy(APPOINTMENT_SLOT.STARTS_AT.asc(), BOOKING.CREATED_AT.asc())
                ).as("bookings")
                        .convertFrom(rs -> rs.map(bookingRecord -> new BookingDto(
                                bookingRecord.get(BOOKING.ID),

                                bookingRecord.get(BOOKING.STATUS).toString(),
                                bookingRecord.get(BOOKING.CREATED_AT),
                                bookingRecord.get(FACILITY.NAME),
                                bookingRecord.get(APPOINTMENT_SLOT.STARTS_AT),
                                bookingRecord.get(STAFF.HANDLE)
                        )));

        Field<List<LabResultDto>> results =
                multiset(
                        select(
                                LAB_RESULT.RESULT_STATUS,
                                LAB_RESULT.PUBLISHED_AT
                        )
                                .from(LAB_RESULT)
                                .where(LAB_RESULT.LAB_ORDER_ID.eq(LAB_ORDER.ID))
                                .orderBy(LAB_RESULT.PUBLISHED_AT.asc().nullsLast())
                ).as("results")
                        .convertFrom(rs -> rs.map(resultRecord -> new LabResultDto(
                                resultRecord.get(LAB_RESULT.RESULT_STATUS).toString(),
                                resultRecord.get(LAB_RESULT.PUBLISHED_AT)
                        )));

        Field<List<LabOrderDto>> labOrders =
                multiset(
                        select(
                                LAB_ORDER.ID,
                                LAB_ORDER.TEST_CODE,
                                FACILITY.NAME,
                                LAB_ORDER.ORDERED_AT,
                                results)
                                .from(LAB_ORDER)
                                .join(FACILITY).on(FACILITY.ID.eq(LAB_ORDER.LAB_FACILITY_ID))
                                .where(LAB_ORDER.TRIAGE_CASE_ID.eq(TRIAGE_CASE.ID))
                                .orderBy(LAB_ORDER.ORDERED_AT.asc())
                ).as("lab_orders")
                        .convertFrom(rs -> rs.map(labOrderRecord -> new LabOrderDto(
                                labOrderRecord.get(LAB_ORDER.ID),
                                labOrderRecord.get(LAB_ORDER.TEST_CODE),
                                labOrderRecord.get(FACILITY.NAME),
                                labOrderRecord.get(LAB_ORDER.ORDERED_AT),
                                labOrderRecord.get(results)
                        )));

        return db.select(
                        TRIAGE_CASE.ID,
                        TRIAGE_CASE.STATUS,
                        TRIAGE_CASE.CREATED_AT,
                        TRIAGE_CASE.SEVERITY,
                        PATIENT.PUBLIC_REF,
                        FACILITY.NAME,
                        SPECIALTY.NAME,
                        bookings,
                        labOrders
                )
                .from(TRIAGE_CASE)
                .join(PATIENT).on(PATIENT.ID.eq(TRIAGE_CASE.PATIENT_ID))
                .join(FACILITY).on(FACILITY.ID.eq(TRIAGE_CASE.INTAKE_FACILITY_ID))
                .join(SPECIALTY).on(SPECIALTY.ID.eq(TRIAGE_CASE.REQUIRED_SPECIALTY_ID))
                .where(TRIAGE_CASE.ID.eq(triageCaseId))
                .fetchOptional(triageRecord -> new TriageCaseDetailsResponse(
                        triageRecord.get(TRIAGE_CASE.ID),
                        triageRecord.get(TRIAGE_CASE.STATUS).toString(),
                        triageRecord.get(TRIAGE_CASE.CREATED_AT),
                        triageRecord.get(TRIAGE_CASE.SEVERITY),
                        triageRecord.get(PATIENT.PUBLIC_REF),
                        triageRecord.get(FACILITY.NAME),
                        triageRecord.get(SPECIALTY.NAME),
                        triageRecord.get(bookings),
                        triageRecord.get(labOrders)
                ));

    }

}
