package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tn.iteam.meduserservice.controllers.specs.IProfileController;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.services.specs.IUserService;

@RestController
@RequiredArgsConstructor
public class ProfileController implements IProfileController {
    private final IUserService userService;

    @Override
    public ResponseEntity<DoctorDto> getDoctorProfileDetails(String doctorId) {
        return ResponseEntity.ok(userService.getDoctorProfile(doctorId));
    }

    @Override
    public ResponseEntity<PatientDto> getPatientProfileDetails(String patientId) {
        return ResponseEntity.ok(userService.getPatientProfile(patientId));
    }

    @Override
    public ResponseEntity<DoctorDto> getInternalDoctorProfile(String doctorId) {
        return ResponseEntity.ok(userService.getDoctorProfile(doctorId));
    }

    @Override
    public ResponseEntity<PatientDto> getInternalPatientProfile(String patientId) {
        return ResponseEntity.ok(userService.getPatientProfile(patientId));
    }

    @Override
    public ResponseEntity<DoctorDto> uploadDoctorProfileImage(String doctorId, MultipartFile image) {
        return ResponseEntity.ok(userService.updateDoctorProfileImage(doctorId, image));
    }

    @Override
    public ResponseEntity<PatientDto> uploadPatientProfileImage(String patientId, MultipartFile image) {
        return ResponseEntity.ok(userService.updatePatientProfileImage(patientId, image));
    }
}
