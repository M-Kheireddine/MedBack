package tn.iteam.medcoreservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.medcoreservice.controllers.specs.IPrescriptionController;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.services.impls.IPrescriptionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PrescriptionController implements IPrescriptionController {
    private final IPrescriptionService prescriptionService;

    @Override
    public ResponseEntity<PrescriptionResponseDto> createPrescription(PrescriptionRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.createPrescription(requestDto));
    }

    @Override
    public ResponseEntity<List<PrescriptionResponseDto>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @Override
    public ResponseEntity<PrescriptionResponseDto> getPrescriptionById(String prescriptionId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(prescriptionId));
    }

    @Override
    public ResponseEntity<List<PrescriptionResponseDto>> getPrescriptionsByDoctorId(String doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorId(doctorId));
    }

    @Override
    public ResponseEntity<List<PrescriptionResponseDto>> getPrescriptionsByPatientId(String patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId));
    }

    @Override
    public ResponseEntity<Void> deletePrescription(String prescriptionId) {
        prescriptionService.deletePrescription(prescriptionId);
        return ResponseEntity.noContent().build();
    }
}
