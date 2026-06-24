package tn.iteam.medcoreservice.services.impls;

import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;

import java.util.List;

public interface IMedicationService {
    MedicationResponseDto createMedication(MedicationRequestDto requestDto);

    List<MedicationResponseDto> getAllMedications();

    MedicationResponseDto getMedicationById(String medicationId);

    MedicationResponseDto updateMedication(String medicationId, MedicationRequestDto requestDto);

    void deleteMedication(String medicationId);

    List<MedicationResponseDto> searchMedications(String query);
}
