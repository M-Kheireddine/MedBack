package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.meduserservice.controllers.specs.IDoctorController;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DoctorController implements IDoctorController {
    private final IUserService userService;

    @Override
    public ResponseEntity<List<DoctorDto>> getPublicDoctors(String search, String specialty) {
        return ResponseEntity.ok(userService.searchPublicDoctors(search, specialty));
    }

    @Override
    public ResponseEntity<DoctorDto> getPublicDoctorById(String doctorId) {
        return ResponseEntity.ok(userService.getDoctorProfile(doctorId));
    }

    @Override
    public ResponseEntity<DoctorDto> getDoctorProfile(String doctorId) {
        return ResponseEntity.ok(userService.getDoctorProfile(doctorId));
    }

    @Override
    public ResponseEntity<DoctorDto> getDoctorSummary(String doctorId) {
        return ResponseEntity.ok(userService.getDoctorSummary(doctorId));
    }
}
