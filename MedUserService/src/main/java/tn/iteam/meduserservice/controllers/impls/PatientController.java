package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.meduserservice.controllers.specs.IPatientController;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.services.specs.IUserService;

@RestController
@RequiredArgsConstructor
public class PatientController implements IPatientController {
    private final IUserService userService;

    @Override
    public ResponseEntity<PatientDto> getPatientProfile(String patientId) {
        return ResponseEntity.ok(userService.getPatientProfile(patientId));
    }

    @Override
    public ResponseEntity<PatientDto> getPatientSummary(String patientId) {
        return ResponseEntity.ok(userService.getPatientSummary(patientId));
    }

    @Override
    public ResponseEntity<PatientDto> unarchivePatient(String patientId) {
        return ResponseEntity.ok(userService.unarchivePatient(patientId));
    }
}
