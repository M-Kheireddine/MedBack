package tn.iteam.medcoreservice.services.specs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.medcoreservice.clients.UserProfileClient;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionLineRequestDto;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionLineDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.PrescriptionDtoMapper;
import tn.iteam.medcoreservice.mappers.PrescriptionMapper;
import tn.iteam.medcoreservice.messaging.NotificationEventPublisher;
import tn.iteam.medcoreservice.models.Medication;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.models.PrescriptionLine;
import tn.iteam.medcoreservice.repositories.MedicationRepository;
import tn.iteam.medcoreservice.repositories.PrescriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    @Mock
    private PrescriptionDtoMapper prescriptionDtoMapper;

    @Mock
    private UserProfileClient userProfileClient;

    @Mock
    private PatientIdentifierResolver patientIdentifierResolver;

    private final PrescriptionMapper prescriptionMapper = new PrescriptionMapper();

    private PrescriptionService buildService() {
        when(patientIdentifierResolver.resolvePrimaryPatientId(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0, String.class));
        when(patientIdentifierResolver.resolveCandidatePatientIds(anyString()))
                .thenAnswer(invocation -> List.of(invocation.getArgument(0, String.class)));
        when(medicationRepository.findAllById(anyList())).thenReturn(List.of());
        when(prescriptionDtoMapper.toPrescriptionLineDto(any(PrescriptionLine.class))).thenAnswer(invocation -> {
            PrescriptionLine line = invocation.getArgument(0, PrescriptionLine.class);
            return PrescriptionLineDto.builder()
                    .medicationId(line.getMedicationId())
                    .dosage(line.getDosage())
                    .duration(line.getDuration())
                    .build();
        });

        return new PrescriptionService(
                prescriptionRepository,
                medicationRepository,
                prescriptionMapper,
                prescriptionDtoMapper,
                notificationEventPublisher,
                userProfileClient,
                patientIdentifierResolver
        );
    }

    @Test
    void createPrescriptionShouldSavePublishEventAndReturnMappedResponse() {
        PrescriptionService prescriptionService = buildService();

        PrescriptionRequestDto requestDto = PrescriptionRequestDto.builder()
                .doctorId("doctor-1")
                .patientId("patient-1")
                .doctorNotes("Take medication after meals.")
                .recipientEmail("patient@example.com")
                .prescriptionLines(List.of(
                        PrescriptionLineRequestDto.builder()
                                .medicationId("med-1")
                                .dosage("1 tablet")
                                .duration("5 days")
                                .build()
                ))
                .build();

        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription prescription = invocation.getArgument(0);
            prescription.setId("prescription-1");
            return prescription;
        });

        PrescriptionResponseDto response = prescriptionService.createPrescription(requestDto);

        ArgumentCaptor<Prescription> prescriptionCaptor = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository).save(prescriptionCaptor.capture());
        Prescription savedPrescription = prescriptionCaptor.getValue();

        assertEquals("doctor-1", savedPrescription.getDoctorId());
        assertEquals("patient-1", savedPrescription.getPatientId());
        assertEquals("Take medication after meals.", savedPrescription.getDoctorNotes());
        assertEquals(1, savedPrescription.getPrescriptionLines().size());
        assertEquals("med-1", savedPrescription.getPrescriptionLines().get(0).getMedicationId());

        verify(notificationEventPublisher).publishPrescriptionCreated(savedPrescription, "patient@example.com");

        assertEquals("prescription-1", response.getId());
        assertEquals("doctor-1", response.getDoctorId());
        assertEquals("patient-1", response.getPatientId());
        assertEquals(1, response.getPrescriptionLines().size());
    }

    @Test
    void getAllPrescriptionsShouldReturnMappedRepositoryResults() {
        PrescriptionService prescriptionService = buildService();

        Prescription latest = prescription("prescription-1", "doctor-1", "patient-1");
        Prescription older = prescription("prescription-2", "doctor-2", "patient-2");
        when(prescriptionRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(latest, older));

        List<PrescriptionResponseDto> response = prescriptionService.getAllPrescriptions();

        assertEquals(2, response.size());
        assertEquals("prescription-1", response.get(0).getId());
        assertEquals("prescription-2", response.get(1).getId());
    }

    @Test
    void getPrescriptionByIdShouldReturnMappedPrescriptionWhenFound() {
        PrescriptionService prescriptionService = buildService();

        Prescription prescription = prescription("prescription-42", "doctor-42", "patient-42");
        when(prescriptionRepository.findById("prescription-42")).thenReturn(Optional.of(prescription));

        PrescriptionResponseDto response = prescriptionService.getPrescriptionById("prescription-42");

        assertEquals("prescription-42", response.getId());
        assertEquals("doctor-42", response.getDoctorId());
    }

    @Test
    void getPrescriptionByIdShouldThrowWhenPrescriptionDoesNotExist() {
        PrescriptionService prescriptionService = buildService();

        when(prescriptionRepository.findById("missing-prescription")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> prescriptionService.getPrescriptionById("missing-prescription")
        );

        assertEquals("Prescription not found with id: missing-prescription", exception.getMessage());
    }

    @Test
    void getPrescriptionsByDoctorIdShouldReturnMappedResults() {
        PrescriptionService prescriptionService = buildService();

        when(prescriptionRepository.findByDoctorIdOrderByCreatedAtDesc("doctor-7"))
                .thenReturn(List.of(prescription("prescription-7", "doctor-7", "patient-7")));

        List<PrescriptionResponseDto> response = prescriptionService.getPrescriptionsByDoctorId("doctor-7");

        assertEquals(1, response.size());
        assertEquals("doctor-7", response.get(0).getDoctorId());
    }

    @Test
    void getPrescriptionsByPatientIdShouldReturnMappedResults() {
        PrescriptionService prescriptionService = buildService();

        when(prescriptionRepository.findByPatientIdInOrderByCreatedAtDesc(List.of("patient-9")))
                .thenReturn(List.of(prescription("prescription-9", "doctor-9", "patient-9")));

        List<PrescriptionResponseDto> response = prescriptionService.getPrescriptionsByPatientId("patient-9");

        assertEquals(1, response.size());
        assertEquals("patient-9", response.get(0).getPatientId());
    }

    @Test
    void deletePrescriptionShouldDeleteExistingPrescription() {
        PrescriptionService prescriptionService = buildService();

        Prescription prescription = prescription("prescription-77", "doctor-77", "patient-77");
        when(prescriptionRepository.findById("prescription-77")).thenReturn(Optional.of(prescription));

        prescriptionService.deletePrescription("prescription-77");

        verify(prescriptionRepository).delete(prescription);
    }

    private Prescription prescription(String id, String doctorId, String patientId) {
        return Prescription.builder()
                .id(id)
                .doctorId(doctorId)
                .patientId(patientId)
                .createdAt(LocalDateTime.of(2026, 7, 2, 10, 0))
                .doctorNotes("Notes")
                .prescriptionLines(List.of(
                        PrescriptionLine.builder()
                                .medicationId("med-1")
                                .dosage("2 per day")
                                .duration("7 days")
                                .build()
                ))
                .build();
    }
}
