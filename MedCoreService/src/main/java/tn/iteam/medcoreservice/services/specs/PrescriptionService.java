package tn.iteam.medcoreservice.services.specs;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.clients.UserProfileClient;
import tn.iteam.medcoreservice.clients.dto.InternalDoctorProfileDto;
import tn.iteam.medcoreservice.clients.dto.InternalPatientProfileDto;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionDoctorMetadataDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionLineDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionPatientMetadataDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.PrescriptionDtoMapper;
import tn.iteam.medcoreservice.mappers.PrescriptionMapper;
import tn.iteam.medcoreservice.models.Medication;
import tn.iteam.medcoreservice.messaging.NotificationEventPublisher;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.models.PrescriptionLine;
import tn.iteam.medcoreservice.repositories.MedicationRepository;
import tn.iteam.medcoreservice.repositories.PrescriptionRepository;
import tn.iteam.medcoreservice.services.impls.IPrescriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrescriptionService implements IPrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionDtoMapper prescriptionDtoMapper;
    private final NotificationEventPublisher notificationEventPublisher;
    private final UserProfileClient userProfileClient;
    private final PatientIdentifierResolver patientIdentifierResolver;

    @Override
    public PrescriptionResponseDto createPrescription(PrescriptionRequestDto requestDto) {
        Prescription prescription = prescriptionMapper.toPrescription(requestDto);
        prescription.setPatientId(patientIdentifierResolver.resolvePrimaryPatientId(requestDto.getPatientId()));
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        notificationEventPublisher.publishPrescriptionCreated(savedPrescription, requestDto.getRecipientEmail());
        log.info("Prescription created with id={} for doctorId={} patientId={}",
                savedPrescription.getId(), savedPrescription.getDoctorId(), savedPrescription.getPatientId());
        return prescriptionMapper.toPrescriptionResponseDto(savedPrescription);
    }

    @Override
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        return this.prescriptionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(prescriptionMapper::toPrescriptionResponseDto)
                .toList();
    }

    @Override
    public PrescriptionResponseDto getPrescriptionById(String prescriptionId) {
        return prescriptionMapper.toPrescriptionResponseDto(findPrescriptionById(prescriptionId));
    }

    @Override
    public PrescriptionDto getPrescriptionDetails(String prescriptionId) {
        return toPrescriptionDto(findPrescriptionById(prescriptionId));
    }

    @Override
    public List<PrescriptionResponseDto> getPrescriptionsByDoctorId(String doctorId) {
        return prescriptionRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId)
                .stream()
                .map(prescriptionMapper::toPrescriptionResponseDto)
                .toList();
    }

    @Override
    public List<PrescriptionResponseDto> getPrescriptionsByPatientId(String patientId) {
        return prescriptionRepository.findByPatientIdInOrderByCreatedAtDesc(
                        patientIdentifierResolver.resolveCandidatePatientIds(patientId)
                )
                .stream()
                .map(prescriptionMapper::toPrescriptionResponseDto)
                .toList();
    }

    @Override
    public PrescriptionDto updatePrescription(String prescriptionId, PrescriptionRequestDto requestDto) {
        Prescription prescription = findPrescriptionById(prescriptionId);
        Prescription updatedPrescription = prescriptionMapper.toPrescription(requestDto);

        prescription.setDoctorId(updatedPrescription.getDoctorId());
        prescription.setPatientId(patientIdentifierResolver.resolvePrimaryPatientId(requestDto.getPatientId()));
        prescription.setDoctorNotes(updatedPrescription.getDoctorNotes());
        prescription.setPrescriptionLines(updatedPrescription.getPrescriptionLines());
        prescription.setCreatedAt(resolveCreatedAt(prescription.getCreatedAt()));

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        if (requestDto.getRecipientEmail() != null && !requestDto.getRecipientEmail().isBlank()) {
            notificationEventPublisher.publishPrescriptionUpdated(savedPrescription, requestDto.getRecipientEmail());
        }

        log.info("Prescription updated with id={} for doctorId={} patientId={}",
                savedPrescription.getId(), savedPrescription.getDoctorId(), savedPrescription.getPatientId());
        return toPrescriptionDto(savedPrescription);
    }

    @Override
    public void deletePrescription(String prescriptionId) {
        Prescription prescription = findPrescriptionById(prescriptionId);
        prescriptionRepository.delete(prescription);
        log.info("Prescription deleted with id={}", prescriptionId);
    }

    private Prescription findPrescriptionById(String prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + prescriptionId));
    }

    private PrescriptionDto toPrescriptionDto(Prescription prescription) {
        Map<String, Medication> medicationById = medicationRepository.findAllById(resolveMedicationIds(prescription.getPrescriptionLines()))
                .stream()
                .collect(java.util.stream.Collectors.toMap(Medication::getId, Function.identity()));

        return PrescriptionDto.builder()
                .id(prescription.getId())
                .doctorId(prescription.getDoctorId())
                .patientId(prescription.getPatientId())
                .createdAt(prescription.getCreatedAt())
                .doctorNotes(prescription.getDoctorNotes())
                .doctor(resolveDoctorMetadata(prescription.getDoctorId()).orElse(null))
                .patient(resolvePatientMetadata(prescription.getPatientId()).orElse(null))
                .prescriptionLines(toPrescriptionLineDtos(prescription.getPrescriptionLines(), medicationById))
                .build();
    }

    private List<String> resolveMedicationIds(List<PrescriptionLine> prescriptionLines) {
        if (prescriptionLines == null || prescriptionLines.isEmpty()) {
            return List.of();
        }

        return prescriptionLines.stream()
                .map(PrescriptionLine::getMedicationId)
                .filter(medicationId -> medicationId != null && !medicationId.isBlank())
                .distinct()
                .toList();
    }

    private List<PrescriptionLineDto> toPrescriptionLineDtos(List<PrescriptionLine> prescriptionLines, Map<String, Medication> medicationById) {
        if (prescriptionLines == null || prescriptionLines.isEmpty()) {
            return List.of();
        }

        return prescriptionLines.stream()
                .map(line -> {
                    PrescriptionLineDto lineDto = prescriptionDtoMapper.toPrescriptionLineDto(line);
                    Medication medication = medicationById.get(line.getMedicationId());
                    lineDto.setMedicationName(medication == null ? null : medication.getName());
                    return lineDto;
                })
                .toList();
    }

    private LocalDateTime resolveCreatedAt(LocalDateTime createdAt) {
        return createdAt == null ? LocalDateTime.now() : createdAt;
    }

    private Optional<PrescriptionDoctorMetadataDto> resolveDoctorMetadata(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            return Optional.empty();
        }

        try {
            InternalDoctorProfileDto doctorProfile = userProfileClient.getDoctorProfile(doctorId);
            return Optional.of(PrescriptionDoctorMetadataDto.builder()
                    .id(doctorProfile.getId())
                    .firstName(doctorProfile.getFirstName())
                    .lastName(doctorProfile.getLastName())
                    .email(doctorProfile.getEmail())
                    .specialty(doctorProfile.getSpecialty())
                    .phoneNumber(doctorProfile.getPhoneNumber())
                    .clinicAddress(doctorProfile.getClinicAddress())
                    .medicalLicenseNumber(doctorProfile.getMedicalLicenseNumber())
                    .profileImageUrl(doctorProfile.getProfileImageUrl())
                    .build());
        } catch (Exception exception) {
            log.warn("Unable to resolve doctor metadata for doctorId={}: {}", doctorId, exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<PrescriptionPatientMetadataDto> resolvePatientMetadata(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return Optional.empty();
        }

        try {
            InternalPatientProfileDto patientProfile = userProfileClient.getPatientProfile(patientId);
            return Optional.of(PrescriptionPatientMetadataDto.builder()
                    .id(patientProfile.getId())
                    .functionalId(patientProfile.getFunctionalId())
                    .firstName(patientProfile.getFirstName())
                    .lastName(patientProfile.getLastName())
                    .email(patientProfile.getEmail())
                    .birthDate(patientProfile.getBirthDate())
                    .socialSecurityNumber(patientProfile.getSocialSecurityNumber())
                    .bloodType(patientProfile.getBloodType())
                    .profileImageUrl(patientProfile.getProfileImageUrl())
                    .build());
        } catch (Exception exception) {
            log.warn("Unable to resolve patient metadata for patientId={}: {}", patientId, exception.getMessage());
            return Optional.empty();
        }
    }
}
