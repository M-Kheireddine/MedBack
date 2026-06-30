package tn.iteam.medcoreservice.controllers.specs;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationAutocompleteDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IMedicationController {
    @PostMapping(path = ApiUtils.API_ADMIN_CREATE_MEDICATION, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MedicationResponseDto> createAdminMedication(
            @Valid @RequestPart("medication") MedicationRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    );

    @GetMapping(ApiUtils.API_ADMIN_GET_ALL_MEDICATIONS)
    ResponseEntity<List<MedicationResponseDto>> getAdminMedications();

    @GetMapping(ApiUtils.API_ADMIN_GET_MEDICATION_BY_ID)
    ResponseEntity<MedicationResponseDto> getAdminMedicationById(@PathVariable("medicationId") String medicationId);

    @PutMapping(path = ApiUtils.API_ADMIN_UPDATE_MEDICATION, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MedicationResponseDto> updateAdminMedication(
            @PathVariable("medicationId") String medicationId,
            @Valid @RequestPart("medication") MedicationRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    );

    @DeleteMapping(ApiUtils.API_ADMIN_DELETE_MEDICATION)
    ResponseEntity<Void> deleteAdminMedication(@PathVariable("medicationId") String medicationId);

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
    ResponseEntity<List<MedicationResponseDto>> getPublicMedications(@RequestParam(value = "search", required = false) String search);

    @GetMapping(ApiUtils.API_PUBLIC_GET_MEDICATION_BY_ID)
    ResponseEntity<MedicationResponseDto> getPublicMedicationById(@PathVariable("medicationId") String medicationId);

    @GetMapping(ApiUtils.API_PUBLIC_SEARCH_MEDICATIONS)
    ResponseEntity<List<MedicationResponseDto>> searchPublicMedications(@RequestParam(value = "query", required = false) String query);

    @GetMapping(ApiUtils.API_MEDICATION_AUTOCOMPLETE)
    ResponseEntity<List<MedicationAutocompleteDto>> autocompleteMedications(@RequestParam("query") String query);
}
