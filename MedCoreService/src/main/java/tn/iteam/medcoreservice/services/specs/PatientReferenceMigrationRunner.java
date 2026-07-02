package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.repositories.AppointmentRepository;
import tn.iteam.medcoreservice.repositories.PrescriptionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientReferenceMigrationRunner implements ApplicationRunner {
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientIdentifierResolver patientIdentifierResolver;

    @Override
    public void run(ApplicationArguments args) {
        migrateAppointments();
        migratePrescriptions();
    }

    private void migrateAppointments() {
        Map<String, String> resolvedIds = new HashMap<>();
        long migratedCount = 0L;

        for (Appointment appointment : appointmentRepository.findAll()) {
            String currentPatientId = appointment.getPatientId();
            String normalizedPatientId = resolveNormalizedPatientId(currentPatientId, resolvedIds);

            if (!Objects.equals(currentPatientId, normalizedPatientId)) {
                appointment.setPatientId(normalizedPatientId);
                appointmentRepository.save(appointment);
                migratedCount++;
            }
        }

        if (migratedCount > 0) {
            log.info("Migrated {} appointments to patient functional identifiers.", migratedCount);
        }
    }

    private void migratePrescriptions() {
        Map<String, String> resolvedIds = new HashMap<>();
        long migratedCount = 0L;

        for (Prescription prescription : prescriptionRepository.findAll()) {
            String currentPatientId = prescription.getPatientId();
            String normalizedPatientId = resolveNormalizedPatientId(currentPatientId, resolvedIds);

            if (!Objects.equals(currentPatientId, normalizedPatientId)) {
                prescription.setPatientId(normalizedPatientId);
                prescriptionRepository.save(prescription);
                migratedCount++;
            }
        }

        if (migratedCount > 0) {
            log.info("Migrated {} prescriptions to patient functional identifiers.", migratedCount);
        }
    }

    private String resolveNormalizedPatientId(String patientId, Map<String, String> resolvedIds) {
        if (patientId == null || patientId.isBlank() || patientId.startsWith("PAT-")) {
            return patientId;
        }

        return resolvedIds.computeIfAbsent(patientId, patientIdentifierResolver::resolvePrimaryPatientId);
    }
}
