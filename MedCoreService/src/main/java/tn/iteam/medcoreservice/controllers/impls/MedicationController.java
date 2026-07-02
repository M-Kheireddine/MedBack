package tn.iteam.medcoreservice.controllers.impls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.medcoreservice.controllers.specs.IMedicationController;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationAutocompleteDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.services.impls.IMedicationService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class MedicationController implements IMedicationController {
    private final IMedicationService medicationService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Override
    public ResponseEntity<MedicationResponseDto> createAdminMedication(String medicationJson, MultipartFile imageFile) {
        MedicationRequestDto requestDto = parseMedicationRequest(medicationJson);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicationService.createAdminMedication(requestDto, imageFile));
    }

    @Override
    public ResponseEntity<List<MedicationResponseDto>> getAdminMedications() {
        return ResponseEntity.ok(medicationService.getAllMedications());
    }

    @Override
    public ResponseEntity<MedicationResponseDto> getAdminMedicationById(String medicationId) {
        return ResponseEntity.ok(medicationService.getMedicationById(medicationId));
    }

    @Override
    public ResponseEntity<MedicationResponseDto> updateAdminMedication(String medicationId, String medicationJson, MultipartFile imageFile) {
        MedicationRequestDto requestDto = parseMedicationRequest(medicationJson);
        return ResponseEntity.ok(medicationService.updateAdminMedication(medicationId, requestDto, imageFile));
    }

    @Override
    public ResponseEntity<Void> deleteAdminMedication(String medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MedicationResponseDto> createMedication(MedicationRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicationService.createMedication(requestDto));
    }

    @Override
    public ResponseEntity<List<MedicationResponseDto>> getAllMedications() {
        return ResponseEntity.ok(medicationService.getAllMedications());
    }

    @Override
    public ResponseEntity<MedicationResponseDto> getMedicationById(String medicationId) {
        return ResponseEntity.ok(medicationService.getMedicationById(medicationId));
    }

    @Override
    public ResponseEntity<MedicationResponseDto> updateMedication(String medicationId, MedicationRequestDto requestDto) {
        return ResponseEntity.ok(medicationService.updateMedication(medicationId, requestDto));
    }

    @Override
    public ResponseEntity<Void> deleteMedication(String medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<MedicationResponseDto>> searchMedications(String query) {
        return ResponseEntity.ok(medicationService.searchMedications(query));
    }

    @Override
    public ResponseEntity<List<MedicationResponseDto>> getPublicMedications(String search) {
        return ResponseEntity.ok(medicationService.searchMedications(search));
    }

    @Override
    public ResponseEntity<MedicationResponseDto> getPublicMedicationById(String medicationId) {
        return ResponseEntity.ok(medicationService.getMedicationById(medicationId));
    }

    @Override
    public ResponseEntity<List<MedicationResponseDto>> searchPublicMedications(String query) {
        return ResponseEntity.ok(medicationService.searchMedications(query));
    }

    @Override
    public ResponseEntity<List<MedicationDto>> getMedicationCatalog(String search) {
        return ResponseEntity.ok(medicationService.getMedicationCatalog(search));
    }

    @Override
    public ResponseEntity<MedicationDto> getMedicationCatalogById(String medicationId) {
        return ResponseEntity.ok(medicationService.getMedicationCatalogById(medicationId));
    }

    @Override
    public ResponseEntity<List<MedicationAutocompleteDto>> autocompleteMedications(String query) {
        return ResponseEntity.ok(medicationService.autocompleteMedications(query));
    }

    private MedicationRequestDto parseMedicationRequest(String medicationJson) {
        if (medicationJson == null || medicationJson.isBlank()) {
            throw new IllegalArgumentException("medication part is required.");
        }

        try {
            MedicationRequestDto requestDto = objectMapper.readValue(medicationJson, MedicationRequestDto.class);
            validateMedicationRequest(requestDto);
            return requestDto;
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("medication part must contain valid JSON.", exception);
        }
    }

    private void validateMedicationRequest(MedicationRequestDto requestDto) {
        Set<ConstraintViolation<MedicationRequestDto>> violations = validator.validate(requestDto);
        if (violations.isEmpty()) {
            return;
        }

        String message = violations.stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("Invalid medication payload.");

        throw new IllegalArgumentException(message);
    }
}
