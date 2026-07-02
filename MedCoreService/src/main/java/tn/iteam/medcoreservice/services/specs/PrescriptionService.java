package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionLineDto;
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

    @Override
    public PrescriptionResponseDto createPrescription(PrescriptionRequestDto requestDto) {
        Prescription prescription = prescriptionMapper.toPrescription(requestDto);
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
        return prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(prescriptionMapper::toPrescriptionResponseDto)
                .toList();
    }

    @Override
    public PrescriptionDto updatePrescription(String prescriptionId, PrescriptionRequestDto requestDto) {
        Prescription prescription = findPrescriptionById(prescriptionId);
        Prescription updatedPrescription = prescriptionMapper.toPrescription(requestDto);

        prescription.setDoctorId(updatedPrescription.getDoctorId());
        prescription.setPatientId(updatedPrescription.getPatientId());
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
}
