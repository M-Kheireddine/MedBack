package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.PrescriptionMapper;
import tn.iteam.medcoreservice.messaging.NotificationEventPublisher;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.repositories.PrescriptionRepository;
import tn.iteam.medcoreservice.services.impls.IPrescriptionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrescriptionService implements IPrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
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
    public void deletePrescription(String prescriptionId) {
        Prescription prescription = findPrescriptionById(prescriptionId);
        prescriptionRepository.delete(prescription);
        log.info("Prescription deleted with id={}", prescriptionId);
    }

    private Prescription findPrescriptionById(String prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + prescriptionId));
    }
}
