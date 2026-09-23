-- Seed data

INSERT INTO district(name) VALUES
                               ('Kabuki'),
                               ('Harbor'),
                               ('Downtown'),
                               ('Neon Heights');

INSERT INTO facility(name, type, district_id) VALUES
                                                  ('Kabuki Street Clinic', 'CLINIC', (SELECT id FROM district WHERE name='Kabuki')),
                                                  ('Harbor Diagnostics Lab', 'LAB', (SELECT id FROM district WHERE name='Harbor')),
                                                  ('Downtown General Hospital', 'HOSPITAL', (SELECT id FROM district WHERE name='Downtown')),
                                                  ('NeonCare Mobile Pod #7', 'MOBILE', (SELECT id FROM district WHERE name='Neon Heights'));

INSERT INTO facility(name, type, district_id)
SELECT 'Harbor Community Clinic', 'CLINIC', d.id
FROM district d
WHERE d.name = 'Harbor'
    ON CONFLICT (name) DO NOTHING;

-- Transfer graph
INSERT INTO facility_link(from_facility_id, to_facility_id, travel_minutes) VALUES
                                                                                ((SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                 (SELECT id FROM facility WHERE name='Downtown General Hospital'), 18),
                                                                                ((SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                 (SELECT id FROM facility WHERE name='Harbor Diagnostics Lab'), 14),
                                                                                ((SELECT id FROM facility WHERE name='Harbor Diagnostics Lab'),
                                                                                 (SELECT id FROM facility WHERE name='Downtown General Hospital'), 16),
                                                                                ((SELECT id FROM facility WHERE name='NeonCare Mobile Pod #7'),
                                                                                 (SELECT id FROM facility WHERE name='Kabuki Street Clinic'), 11),
                                                                                ((SELECT id FROM facility WHERE name='NeonCare Mobile Pod #7'),
                                                                                 (SELECT id FROM facility WHERE name='Downtown General Hospital'), 22);

INSERT INTO specialty(name) VALUES
                                ('General Medicine'),
                                ('Cardiology'),
                                ('Radiology'),
                                ('Pathology');

-- Staff
INSERT INTO staff(facility_id, specialty_id, handle, active) VALUES
                                                                 ((SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                  (SELECT id FROM specialty WHERE name='General Medicine'), 'doc_kabuki_01', TRUE),

                                                                 ((SELECT id FROM facility WHERE name='Downtown General Hospital'),
                                                                  (SELECT id FROM specialty WHERE name='Cardiology'), 'cardio_dt_01', TRUE),
                                                                 ((SELECT id FROM facility WHERE name='Downtown General Hospital'),
                                                                  (SELECT id FROM specialty WHERE name='Radiology'), 'radio_dt_01', TRUE),

                                                                 ((SELECT id FROM facility WHERE name='Harbor Diagnostics Lab'),
                                                                  (SELECT id FROM specialty WHERE name='Pathology'), 'path_harbor_01', TRUE),

                                                                 ((SELECT id FROM facility WHERE name='NeonCare Mobile Pod #7'),
                                                                  (SELECT id FROM specialty WHERE name='General Medicine'), 'med_pod7_01', TRUE);

-- Optional: add GM staff for the new clinic
INSERT INTO staff(facility_id, specialty_id, handle, active)
SELECT f.id, s.id, 'doc_harbor_01', TRUE
FROM facility f
         JOIN specialty s ON s.name = 'General Medicine'
WHERE f.name = 'Harbor Community Clinic'
    ON CONFLICT (facility_id, handle) DO NOTHING;

-- Patients (synthetic IDs)
INSERT INTO patient(public_ref, created_at) VALUES
                                                ('P-83K2X', '2026-01-19 09:10:00+00'),
                                                ('P-19Q7M', '2026-01-19 12:40:00+00'),
                                                ('P-5ZZ1A', '2026-01-20 07:05:00+00'),
                                                ('P-4N0VQ', '2026-01-20 07:20:00+00'),
                                                ('P-2A7TT', '2026-01-20 07:40:00+00'),
                                                ('P-7K9L2', '2026-01-20 08:10:00+00'),
                                                ('P-0M3X8', '2026-01-20 08:12:00+00'),
                                                ('P-4J2QA', '2026-01-20 08:15:00+00'),
                                                ('P-9T1RN', '2026-01-20 08:18:00+00'),
                                                ('P-6C0PP', '2026-01-20 08:20:00+00');

-- Triage cases
INSERT INTO triage_case(patient_id, intake_facility_id, required_specialty_id, severity, created_at, status) VALUES
                                                                                                                 ((SELECT id FROM patient WHERE public_ref='P-83K2X'),
                                                                                                                  (SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                                                  (SELECT id FROM specialty WHERE name='General Medicine'),
                                                                                                                  2, '2026-01-20 06:30:00+00', 'WAITING'),

                                                                                                                 ((SELECT id FROM patient WHERE public_ref='P-19Q7M'),
                                                                                                                  (SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                                                  (SELECT id FROM specialty WHERE name='General Medicine'),
                                                                                                                  4, '2026-01-20 06:50:00+00', 'WAITING'),

                                                                                                                 ((SELECT id FROM patient WHERE public_ref='P-5ZZ1A'),
                                                                                                                  (SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                                                  (SELECT id FROM specialty WHERE name='General Medicine'),
                                                                                                                  5, '2026-01-20 07:10:00+00', 'WAITING'),

                                                                                                                 ((SELECT id FROM patient WHERE public_ref='P-4N0VQ'),
                                                                                                                  (SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                                                  (SELECT id FROM specialty WHERE name='Cardiology'),
                                                                                                                  3, '2026-01-20 07:15:00+00', 'REFERRED'),

                                                                                                                 ((SELECT id FROM patient WHERE public_ref='P-2A7TT'),
                                                                                                                  (SELECT id FROM facility WHERE name='Downtown General Hospital'),
                                                                                                                  (SELECT id FROM specialty WHERE name='Radiology'),
                                                                                                                  3, '2026-01-20 07:25:00+00', 'IN_TREATMENT'),
                                                                                                                 ((SELECT p.id FROM patient p WHERE p.public_ref = 'P-7K9L2'),
                                                                                                                  (SELECT f.id FROM facility f WHERE f.name = 'Kabuki Street Clinic'),
                                                                                                                  (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
                                                                                                                  3, '2026-01-20 08:25:00+00', 'WAITING'),
                                                                                                                 ((SELECT p.id FROM patient p WHERE p.public_ref = 'P-0M3X8'),
                                                                                                                  (SELECT f.id FROM facility f WHERE f.name = 'Kabuki Street Clinic'),
                                                                                                                  (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
                                                                                                                  2, '2026-01-20 08:26:00+00', 'WAITING'),
                                                                                                                 ((SELECT p.id FROM patient p WHERE p.public_ref = 'P-4J2QA'),
                                                                                                                  (SELECT f.id FROM facility f WHERE f.name = 'Downtown General Hospital'),
                                                                                                                  (SELECT s.id FROM specialty s WHERE s.name = 'Cardiology'),
                                                                                                                  4, '2026-01-20 08:28:00+00', 'WAITING'),
                                                                                                                 ((SELECT p.id FROM patient p WHERE p.public_ref = 'P-9T1RN'),
                                                                                                                  (SELECT f.id FROM facility f WHERE f.name = 'Downtown General Hospital'),
                                                                                                                  (SELECT s.id FROM specialty s WHERE s.name = 'Radiology'),
                                                                                                                  3, '2026-01-20 08:29:00+00', 'WAITING'),
                                                                                                                 ((SELECT p.id FROM patient p WHERE p.public_ref = 'P-6C0PP'),
                                                                                                                  (SELECT f.id FROM facility f WHERE f.name = 'Downtown General Hospital'),
                                                                                                                  (SELECT s.id FROM specialty s WHERE s.name = 'Radiology'),
                                                                                                                  1, '2026-01-20 08:30:00+00', 'WAITING');

-- Appointment slots
INSERT INTO appointment_slot(facility_id, specialty_id, starts_at, ends_at, capacity) VALUES
                                                                                          ((SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                           (SELECT id FROM specialty WHERE name='General Medicine'),
                                                                                           '2026-01-20 09:00:00+00', '2026-01-20 09:20:00+00', 2),

                                                                                          ((SELECT id FROM facility WHERE name='Kabuki Street Clinic'),
                                                                                           (SELECT id FROM specialty WHERE name='General Medicine'),
                                                                                           '2026-01-20 09:20:00+00', '2026-01-20 09:40:00+00', 1),

                                                                                          ((SELECT id FROM facility WHERE name='Downtown General Hospital'),
                                                                                           (SELECT id FROM specialty WHERE name='Cardiology'),
                                                                                           '2026-01-20 10:00:00+00', '2026-01-20 10:30:00+00', 1),

                                                                                          ((SELECT id FROM facility WHERE name='Downtown General Hospital'),
                                                                                           (SELECT id FROM specialty WHERE name='Radiology'),
                                                                                           '2026-01-20 11:00:00+00', '2026-01-20 11:30:00+00', 2);
INSERT INTO appointment_slot(facility_id, specialty_id, starts_at, ends_at, capacity)
VALUES ((SELECT f.id FROM facility f WHERE f.name = 'Kabuki Street Clinic'),
        (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
        '2026-01-20 10:00:00+00', '2026-01-20 10:20:00+00', 1),
       ((SELECT f.id FROM facility f WHERE f.name = 'Kabuki Street Clinic'),
        (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
        '2026-01-20 10:20:00+00', '2026-01-20 10:40:00+00', 1);

INSERT INTO appointment_slot(facility_id, specialty_id, starts_at, ends_at, capacity)
VALUES ((SELECT f.id FROM facility f WHERE f.name = 'Downtown General Hospital'),
        (SELECT s.id FROM specialty s WHERE s.name = 'Cardiology'),
        '2026-01-20 10:30:00+00', '2026-01-20 11:00:00+00', 2);

INSERT INTO appointment_slot(facility_id, specialty_id, starts_at, ends_at, capacity)
VALUES ((SELECT f.id FROM facility f WHERE f.name = 'Downtown General Hospital'),
        (SELECT s.id FROM specialty s WHERE s.name = 'Radiology'),
        '2026-01-20 12:00:00+00', '2026-01-20 12:30:00+00', 2);

INSERT INTO appointment_slot(facility_id, specialty_id, starts_at, ends_at, capacity) VALUES
                                                                                          (
                                                                                              (SELECT f.id FROM facility f WHERE f.name = 'NeonCare Mobile Pod #7'),
                                                                                              (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
                                                                                              '2026-01-20 13:00:00+00', '2026-01-20 13:20:00+00', 1
                                                                                          ),
                                                                                          (
                                                                                              (SELECT f.id FROM facility f WHERE f.name = 'NeonCare Mobile Pod #7'),
                                                                                              (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
                                                                                              '2026-01-20 13:20:00+00', '2026-01-20 13:40:00+00', 2
                                                                                          );


INSERT INTO appointment_slot(facility_id, specialty_id, starts_at, ends_at, capacity) VALUES
                                                                                          (
                                                                                              (SELECT f.id FROM facility f WHERE f.name = 'Harbor Community Clinic'),
                                                                                              (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
                                                                                              '2026-01-20 14:00:00+00', '2026-01-20 14:20:00+00', 1
                                                                                          ),
                                                                                          (
                                                                                              (SELECT f.id FROM facility f WHERE f.name = 'Harbor Community Clinic'),
                                                                                              (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
                                                                                              '2026-01-20 14:20:00+00', '2026-01-20 14:40:00+00', 1
                                                                                          );

INSERT INTO appointment_slot(facility_id, specialty_id, starts_at, ends_at, capacity) VALUES
    (
        (SELECT f.id FROM facility f WHERE f.name = 'Downtown General Hospital'),
        (SELECT s.id FROM specialty s WHERE s.name = 'General Medicine'),
        '2026-01-20 15:00:00+00', '2026-01-20 15:20:00+00', 2
    );

-- Bookings
INSERT INTO booking(triage_case_id, appointment_slot_id, staff_id, status, created_at) VALUES
                                                                                           -- One waiting case gets a reserved slot
                                                                                           (
                                                                                               (SELECT tc.id
                                                                                                FROM triage_case tc
                                                                                                         JOIN patient p ON p.id = tc.patient_id
                                                                                                WHERE p.public_ref = 'P-19Q7M'),
                                                                                               (SELECT s.id
                                                                                                FROM appointment_slot s
                                                                                                         JOIN facility f ON f.id = s.facility_id
                                                                                                         JOIN specialty sp ON sp.id = s.specialty_id
                                                                                                WHERE f.name = 'Kabuki Street Clinic'
                                                                                                  AND sp.name = 'General Medicine'
                                                                                                  AND s.starts_at = '2026-01-20 09:00:00+00'),
                                                                                               (SELECT st.id
                                                                                                FROM staff st
                                                                                                WHERE st.handle = 'doc_kabuki_01'),
                                                                                               'RESERVED',
                                                                                               '2026-01-20 07:00:00+00'
                                                                                           ),

                                                                                           -- Fill up a slot to test capacity logic later
                                                                                           (
                                                                                               (SELECT tc.id
                                                                                                FROM triage_case tc
                                                                                                         JOIN patient p ON p.id = tc.patient_id
                                                                                                WHERE p.public_ref = 'P-83K2X'),
                                                                                               (SELECT s.id
                                                                                                FROM appointment_slot s
                                                                                                         JOIN facility f ON f.id = s.facility_id
                                                                                                         JOIN specialty sp ON sp.id = s.specialty_id
                                                                                                WHERE f.name = 'Kabuki Street Clinic'
                                                                                                  AND sp.name = 'General Medicine'
                                                                                                  AND s.starts_at = '2026-01-20 09:00:00+00'),
                                                                                               (SELECT st.id
                                                                                                FROM staff st
                                                                                                WHERE st.handle = 'doc_kabuki_01'),
                                                                                               'CONFIRMED',
                                                                                               '2026-01-20 07:02:00+00'
                                                                                           );
-- ---------- Bookings for extra slots ----------
-- Kabuki GM 10:00 slot capacity=1 -> FULL (CONFIRMED)
INSERT INTO booking(triage_case_id, appointment_slot_id, staff_id, status, created_at)
VALUES ((SELECT tc.id
         FROM triage_case tc
                  JOIN patient p ON p.id = tc.patient_id
         WHERE p.public_ref = 'P-7K9L2'),
        (SELECT aslt.id
         FROM appointment_slot aslt
                  JOIN facility f ON f.id = aslt.facility_id
                  JOIN specialty s ON s.id = aslt.specialty_id
         WHERE f.name = 'Kabuki Street Clinic'
           AND s.name = 'General Medicine'
           AND aslt.starts_at = '2026-01-20 10:00:00+00'),
        (SELECT st.id FROM staff st WHERE st.handle = 'doc_kabuki_01'),
        'CONFIRMED',
        '2026-01-20 09:10:00+00');

-- Kabuki GM 10:20 slot capacity=1 -> AVAILABLE because booking is CANCELLED (should not count as active)
INSERT INTO booking(triage_case_id, appointment_slot_id, staff_id, status, created_at)
VALUES ((SELECT tc.id
         FROM triage_case tc
                  JOIN patient p ON p.id = tc.patient_id
         WHERE p.public_ref = 'P-0M3X8'),
        (SELECT aslt.id
         FROM appointment_slot aslt
                  JOIN facility f ON f.id = aslt.facility_id
                  JOIN specialty s ON s.id = aslt.specialty_id
         WHERE f.name = 'Kabuki Street Clinic'
           AND s.name = 'General Medicine'
           AND aslt.starts_at = '2026-01-20 10:20:00+00'),
        (SELECT st.id FROM staff st WHERE st.handle = 'doc_kabuki_01'),
        'CANCELLED',
        '2026-01-20 09:12:00+00');

-- Downtown Cardiology 10:30 slot capacity=2 -> AVAILABLE (1 RESERVED booking)
INSERT INTO booking(triage_case_id, appointment_slot_id, staff_id, status, created_at)
VALUES ((SELECT tc.id
         FROM triage_case tc
                  JOIN patient p ON p.id = tc.patient_id
         WHERE p.public_ref = 'P-4J2QA'),
        (SELECT aslt.id
         FROM appointment_slot aslt
                  JOIN facility f ON f.id = aslt.facility_id
                  JOIN specialty s ON s.id = aslt.specialty_id
         WHERE f.name = 'Downtown General Hospital'
           AND s.name = 'Cardiology'
           AND aslt.starts_at = '2026-01-20 10:30:00+00'),
        (SELECT st.id FROM staff st WHERE st.handle = 'cardio_dt_01'),
        'RESERVED',
        '2026-01-20 09:20:00+00');

-- Downtown Radiology 12:00 slot capacity=2 -> FULL (2 active bookings: RESERVED + CONFIRMED)
INSERT INTO booking(triage_case_id, appointment_slot_id, staff_id, status, created_at)
VALUES ((SELECT tc.id
         FROM triage_case tc
                  JOIN patient p ON p.id = tc.patient_id
         WHERE p.public_ref = 'P-9T1RN'),
        (SELECT aslt.id
         FROM appointment_slot aslt
                  JOIN facility f ON f.id = aslt.facility_id
                  JOIN specialty s ON s.id = aslt.specialty_id
         WHERE f.name = 'Downtown General Hospital'
           AND s.name = 'Radiology'
           AND aslt.starts_at = '2026-01-20 12:00:00+00'),
        (SELECT st.id FROM staff st WHERE st.handle = 'radio_dt_01'),
        'RESERVED',
        '2026-01-20 09:25:00+00'),
       ((SELECT tc.id
         FROM triage_case tc
                  JOIN patient p ON p.id = tc.patient_id
         WHERE p.public_ref = 'P-6C0PP'),
        (SELECT aslt.id
         FROM appointment_slot aslt
                  JOIN facility f ON f.id = aslt.facility_id
                  JOIN specialty s ON s.id = aslt.specialty_id
         WHERE f.name = 'Downtown General Hospital'
           AND s.name = 'Radiology'
           AND aslt.starts_at = '2026-01-20 12:00:00+00'),
        (SELECT st.id FROM staff st WHERE st.handle = 'radio_dt_01'),
        'CONFIRMED',
        '2026-01-20 09:26:00+00');


-- Lab orders + results: create “missing result” gap
INSERT INTO lab_order(triage_case_id, lab_facility_id, test_code, ordered_at) VALUES
                                                                                  (
                                                                                      (SELECT tc.id
                                                                                       FROM triage_case tc
                                                                                                JOIN patient p ON p.id = tc.patient_id
                                                                                       WHERE p.public_ref = 'P-4N0VQ'),
                                                                                      (SELECT f.id
                                                                                       FROM facility f
                                                                                       WHERE f.name = 'Harbor Diagnostics Lab'),
                                                                                      'CBC',
                                                                                      '2026-01-20 07:18:00+00'
                                                                                  ),
                                                                                  (
                                                                                      (SELECT tc.id
                                                                                       FROM triage_case tc
                                                                                                JOIN patient p ON p.id = tc.patient_id
                                                                                       WHERE p.public_ref = 'P-2A7TT'),
                                                                                      (SELECT f.id
                                                                                       FROM facility f
                                                                                       WHERE f.name = 'Harbor Diagnostics Lab'),
                                                                                      'XRAY-CHEST',
                                                                                      '2026-01-20 07:30:00+00'
                                                                                  );

INSERT INTO lab_result(lab_order_id, result_status, published_at) VALUES
    (
        (SELECT lo.id
         FROM lab_order lo
                  JOIN triage_case tc ON tc.id = lo.triage_case_id
                  JOIN patient p ON p.id = tc.patient_id
         WHERE p.public_ref = 'P-2A7TT'
           AND lo.test_code = 'XRAY-CHEST'),
        'READY',
        '2026-01-20 08:05:00+00'
    );