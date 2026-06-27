package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.meduserservice.controllers.specs.IUserController;
import tn.iteam.meduserservice.dtos.requests.DoctorRequestDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements IUserController {
    private final IUserService userService;

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Override
    public ResponseEntity<DoctorResponseDto> createDoctor(DoctorRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createDoctor(requestDto));
    }

    @Override
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
        return ResponseEntity.ok(userService.getAllDoctors());
    }

    @Override
    public ResponseEntity<DoctorResponseDto> getDoctorById(String doctorId) {
        return ResponseEntity.ok(userService.getDoctorById(doctorId));
    }

    @Override
    public ResponseEntity<DoctorResponseDto> updateDoctor(String doctorId, DoctorRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateDoctor(doctorId, requestDto));
    }

    @Override
    public ResponseEntity<Void> deleteDoctor(String doctorId) {
        userService.deleteDoctor(doctorId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        return ResponseEntity.ok(userService.getAllPatients());
    }

    @Override
    public ResponseEntity<PatientResponseDto> getPatientById(String patientId) {
        return ResponseEntity.ok(userService.getPatientById(patientId));
    }

    @Override
    public ResponseEntity<PatientResponseDto> archivePatient(String patientId) {
        return ResponseEntity.ok(userService.archivePatient(patientId));
    }
}
