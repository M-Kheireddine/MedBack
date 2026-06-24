package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.MedicationMapper;
import tn.iteam.medcoreservice.models.Medication;
import tn.iteam.medcoreservice.repositories.MedicationRepository;
import tn.iteam.medcoreservice.services.impls.IMedicationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService implements IMedicationService {
    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    @Override
    public MedicationResponseDto createMedication(MedicationRequestDto requestDto) {
        Medication medication = medicationMapper.toMedication(requestDto);
        return medicationMapper.toMedicationResponseDto(medicationRepository.save(medication));
    }

    @Override
    public List<MedicationResponseDto> getAllMedications() {
        return medicationRepository.findAll()
                .stream()
                .map(medicationMapper::toMedicationResponseDto)
                .toList();
    }

    @Override
    public MedicationResponseDto getMedicationById(String medicationId) {
        return medicationMapper.toMedicationResponseDto(findMedicationById(medicationId));
    }

    @Override
    public MedicationResponseDto updateMedication(String medicationId, MedicationRequestDto requestDto) {
        Medication medication = findMedicationById(medicationId);
        medication.setName(requestDto.getName());
        medication.setDescription(requestDto.getDescription());
        medication.setCategory(requestDto.getCategory());
        medication.setLaboratory(requestDto.getLaboratory());
        medication.setImageUrl(requestDto.getImageUrl());
        return medicationMapper.toMedicationResponseDto(medicationRepository.save(medication));
    }

    @Override
    public void deleteMedication(String medicationId) {
        Medication medication = findMedicationById(medicationId);
        medicationRepository.delete(medication);
    }

    @Override
    public List<MedicationResponseDto> searchMedications(String query) {
        List<Medication> medications = query == null || query.isBlank()
                ? medicationRepository.findAll()
                : medicationRepository.search(query.trim());
        return medications.stream()
                .map(medicationMapper::toMedicationResponseDto)
                .toList();
    }

    private Medication findMedicationById(String medicationId) {
        return medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with id: " + medicationId));
    }
}
