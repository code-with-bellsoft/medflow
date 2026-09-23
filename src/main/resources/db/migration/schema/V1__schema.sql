-- MedFlow schema (PostgreSQL)

CREATE TYPE facility_type AS ENUM ('CLINIC', 'HOSPITAL', 'LAB', 'MOBILE');
CREATE TYPE triage_status AS ENUM ('WAITING', 'IN_TREATMENT', 'REFERRED', 'DISCHARGED');
CREATE TYPE booking_status AS ENUM ('RESERVED', 'CONFIRMED', 'CANCELLED', 'NO_SHOW');
CREATE TYPE lab_result_status AS ENUM ('READY', 'PENDING', 'FAILED');

CREATE TABLE district (
                          id          BIGSERIAL PRIMARY KEY,
                          name        VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE facility (
                          id          BIGSERIAL PRIMARY KEY,
                          name        VARCHAR(200) NOT NULL UNIQUE,
                          type        facility_type NOT NULL,
                          district_id BIGINT NOT NULL REFERENCES district(id)
);

-- Directed graph edges: transfers are allowed from -> to
CREATE TABLE facility_link (
                               from_facility_id BIGINT NOT NULL REFERENCES facility(id) ON DELETE CASCADE,
                               to_facility_id   BIGINT NOT NULL REFERENCES facility(id) ON DELETE CASCADE,
                               travel_minutes   INT NOT NULL CHECK (travel_minutes > 0),
                               PRIMARY KEY (from_facility_id, to_facility_id)
);

CREATE INDEX idx_facility_link_to ON facility_link(to_facility_id);

CREATE TABLE specialty (
                           id    BIGSERIAL PRIMARY KEY,
                           name  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE staff (
                       id          BIGSERIAL PRIMARY KEY,
                       facility_id BIGINT NOT NULL REFERENCES facility(id) ON DELETE CASCADE,
                       specialty_id BIGINT NOT NULL REFERENCES specialty(id),
                       handle      VARCHAR(100) NOT NULL,
                       active      BOOLEAN NOT NULL DEFAULT TRUE,
                       UNIQUE (facility_id, handle)
);

CREATE INDEX idx_staff_facility_specialty ON staff(facility_id, specialty_id);

CREATE TABLE patient (
                         id         BIGSERIAL PRIMARY KEY,
                         public_ref VARCHAR(100) NOT NULL UNIQUE,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE triage_case (
                             id                  BIGSERIAL PRIMARY KEY,
                             patient_id          BIGINT NOT NULL REFERENCES patient(id) ON DELETE CASCADE,
                             intake_facility_id  BIGINT NOT NULL REFERENCES facility(id),
                             required_specialty_id BIGINT NOT NULL REFERENCES specialty(id),
                             severity            SMALLINT NOT NULL CHECK (severity BETWEEN 1 AND 5),
                             created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
                             status              triage_status NOT NULL DEFAULT 'WAITING'
);

CREATE INDEX idx_triage_case_queue
    ON triage_case(intake_facility_id, required_specialty_id, status, created_at);

CREATE TABLE appointment_slot (
                                  id          BIGSERIAL PRIMARY KEY,
                                  facility_id BIGINT NOT NULL REFERENCES facility(id) ON DELETE CASCADE,
                                  specialty_id BIGINT NOT NULL REFERENCES specialty(id),
                                  starts_at   TIMESTAMPTZ NOT NULL,
                                  ends_at     TIMESTAMPTZ NOT NULL,
                                  capacity    INT NOT NULL CHECK (capacity > 0),
                                  CHECK (ends_at > starts_at)
);

CREATE INDEX idx_slot_fac_spec_time
    ON appointment_slot(facility_id, specialty_id, starts_at);

CREATE TABLE booking (
                         id                 BIGSERIAL PRIMARY KEY,
                         triage_case_id     BIGINT NOT NULL REFERENCES triage_case(id) ON DELETE CASCADE,
                         appointment_slot_id BIGINT NOT NULL REFERENCES appointment_slot(id) ON DELETE CASCADE,
                         staff_id           BIGINT NULL REFERENCES staff(id),
                         status             booking_status NOT NULL DEFAULT 'RESERVED',
                         created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_booking_case_status ON booking(triage_case_id, status);
CREATE INDEX idx_booking_slot_status ON booking(appointment_slot_id, status);

CREATE TABLE lab_order (
                           id            BIGSERIAL PRIMARY KEY,
                           triage_case_id BIGINT NOT NULL REFERENCES triage_case(id) ON DELETE CASCADE,
                           lab_facility_id BIGINT NOT NULL REFERENCES facility(id),
                           test_code      VARCHAR(100) NOT NULL,
                           ordered_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_lab_order_ordered_at ON lab_order(ordered_at);

CREATE TABLE lab_result (
                            id            BIGSERIAL PRIMARY KEY,
                            lab_order_id  BIGINT NOT NULL UNIQUE REFERENCES lab_order(id) ON DELETE CASCADE,
                            result_status lab_result_status NOT NULL,
                            published_at  TIMESTAMPTZ NULL
);

CREATE INDEX idx_lab_result_status ON lab_result(result_status);