package tn.iteam.medcoreservice.services.specs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.repositories.AppointmentRepository;
import tn.iteam.medcoreservice.repositories.PrescriptionRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientReferenceMigrationRunnerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientIdentifierResolver patientIdentifierResolver;

    private PatientReferenceMigrationRunner migrationRunner;

    @BeforeEach
    void setUp() {
        migrationRunner = new PatientReferenceMigrationRunner(
                appointmentRepository,
                prescriptionRepository,
                patientIdentifierResolver
        );
    }

    @Test
    void runShouldMigrateLegacyAppointmentAndPrescriptionIdentifiers() throws Exception {
        Appointment firstAppointment = Appointment.builder().id("appt-1").patientId("legacy-1").doctorId("doctor-1").build();
        Appointment secondAppointment = Appointment.builder().id("appt-2").patientId("legacy-1").doctorId("doctor-1").build();
        Appointment alreadyNormalizedAppointment = Appointment.builder().id("appt-3").patientId("PAT-500-600-700").doctorId("doctor-1").build();

        Prescription firstPrescription = Prescription.builder().id("rx-1").patientId("legacy-2").doctorId("doctor-1").build();
        Prescription blankPrescription = Prescription.builder().id("rx-2").patientId(" ").doctorId("doctor-1").build();

        when(appointmentRepository.findAll()).thenReturn(List.of(firstAppointment, secondAppointment, alreadyNormalizedAppointment));
        when(prescriptionRepository.findAll()).thenReturn(List.of(firstPrescription, blankPrescription));
        when(patientIdentifierResolver.resolvePrimaryPatientId("legacy-1")).thenReturn("PAT-100-200-300");
        when(patientIdentifierResolver.resolvePrimaryPatientId("legacy-2")).thenReturn("PAT-400-500-600");

        migrationRunner.run(null);

        verify(patientIdentifierResolver, times(1)).resolvePrimaryPatientId("legacy-1");
        verify(patientIdentifierResolver, times(1)).resolvePrimaryPatientId("legacy-2");
        verify(appointmentRepository, times(2)).save(any(Appointment.class));
        verify(prescriptionRepository).save(any(Prescription.class));
        verify(appointmentRepository, never()).save(alreadyNormalizedAppointment);
        verify(prescriptionRepository, never()).save(blankPrescription);
    }
}
