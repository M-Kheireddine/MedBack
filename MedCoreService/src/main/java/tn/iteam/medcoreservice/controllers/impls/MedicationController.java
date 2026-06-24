package tn.iteam.medcoreservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.medcoreservice.controllers.specs.IMedicationController;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.services.impls.IMedicationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MedicationController implements IMedicationController {
    private final IMedicationService medicationService;

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
    public ResponseEntity<List<MedicationResponseDto>> getPublicMedications() {
        return ResponseEntity.ok(medicationService.getAllMedications());
    }

    @Override
    public ResponseEntity<List<MedicationResponseDto>> searchPublicMedications(String query) {
        return ResponseEntity.ok(medicationService.searchMedications(query));
    }
}
