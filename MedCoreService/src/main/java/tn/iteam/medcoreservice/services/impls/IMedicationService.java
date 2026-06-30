package tn.iteam.medcoreservice.services.impls;

import org.springframework.web.multipart.MultipartFile;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationAutocompleteDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;

import java.util.List;

public interface IMedicationService {
    MedicationResponseDto createMedication(MedicationRequestDto requestDto);

    MedicationResponseDto createAdminMedication(MedicationRequestDto requestDto, MultipartFile imageFile);

    List<MedicationResponseDto> getAllMedications();

    MedicationResponseDto getMedicationById(String medicationId);

    MedicationResponseDto updateMedication(String medicationId, MedicationRequestDto requestDto);

    MedicationResponseDto updateAdminMedication(String medicationId, MedicationRequestDto requestDto, MultipartFile imageFile);

    void deleteMedication(String medicationId);

    List<MedicationResponseDto> searchMedications(String query);

    List<MedicationAutocompleteDto> autocompleteMedications(String query);
}
