package tn.iteam.medcoreservice.controllers.specs;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IPrescriptionController {
    @PostMapping(ApiUtils.API_CREATE_PRESCRIPTION)
    ResponseEntity<PrescriptionResponseDto> createPrescription(@Valid @RequestBody PrescriptionRequestDto requestDto);

    @GetMapping(ApiUtils.API_GET_ALL_PRESCRIPTIONS)
    ResponseEntity<List<PrescriptionResponseDto>> getAllPrescriptions();

    @GetMapping(ApiUtils.API_GET_PRESCRIPTION_BY_ID)
    ResponseEntity<PrescriptionResponseDto> getPrescriptionById(@PathVariable("prescriptionId") String prescriptionId);

    @GetMapping(ApiUtils.API_GET_PRESCRIPTIONS_BY_DOCTOR)
    ResponseEntity<List<PrescriptionResponseDto>> getPrescriptionsByDoctorId(@PathVariable("doctorId") String doctorId);

    @GetMapping(ApiUtils.API_GET_PRESCRIPTIONS_BY_PATIENT)
    ResponseEntity<List<PrescriptionResponseDto>> getPrescriptionsByPatientId(@PathVariable("patientId") String patientId);

    @DeleteMapping(ApiUtils.API_DELETE_PRESCRIPTION)
    ResponseEntity<Void> deletePrescription(@PathVariable("prescriptionId") String prescriptionId);
}
