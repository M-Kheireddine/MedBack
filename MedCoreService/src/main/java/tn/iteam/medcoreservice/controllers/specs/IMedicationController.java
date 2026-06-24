package tn.iteam.medcoreservice.controllers.specs;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IMedicationController {
    @PostMapping(ApiUtils.API_CREATE_MEDICATION)
    ResponseEntity<MedicationResponseDto> createMedication(@Valid @RequestBody MedicationRequestDto requestDto);

    @GetMapping(ApiUtils.API_GET_ALL_MEDICATIONS)
    ResponseEntity<List<MedicationResponseDto>> getAllMedications();

    @GetMapping(ApiUtils.API_GET_MEDICATION_BY_ID)
    ResponseEntity<MedicationResponseDto> getMedicationById(@PathVariable("medicationId") String medicationId);

    @PutMapping(ApiUtils.API_UPDATE_MEDICATION)
    ResponseEntity<MedicationResponseDto> updateMedication(@PathVariable("medicationId") String medicationId,
                                                           @Valid @RequestBody MedicationRequestDto requestDto);

    @DeleteMapping(ApiUtils.API_DELETE_MEDICATION)
    ResponseEntity<Void> deleteMedication(@PathVariable("medicationId") String medicationId);

    @GetMapping(ApiUtils.API_SEARCH_MEDICATIONS)
    ResponseEntity<List<MedicationResponseDto>> searchMedications(@RequestParam(value = "query", required = false) String query);

    @GetMapping(ApiUtils.API_PUBLIC_GET_ALL_MEDICATIONS)
    ResponseEntity<List<MedicationResponseDto>> getPublicMedications();

    @GetMapping(ApiUtils.API_PUBLIC_SEARCH_MEDICATIONS)
    ResponseEntity<List<MedicationResponseDto>> searchPublicMedications(@RequestParam(value = "query", required = false) String query);
}
