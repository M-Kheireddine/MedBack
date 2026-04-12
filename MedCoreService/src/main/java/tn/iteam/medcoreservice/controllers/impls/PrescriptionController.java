package tn.iteam.medcoreservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.medcoreservice.controllers.specs.IPrescriptionController;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.services.impls.IPrescriptionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PrescriptionController implements IPrescriptionController {
    private final IPrescriptionService prescriptionService;

    @Override
    public ResponseEntity<List<PrescriptionResponseDto>> getAlPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @Override
    public ResponseEntity<PrescriptionResponseDto> getPrescriptionById(String prescriptionId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(prescriptionId));
    }
}
