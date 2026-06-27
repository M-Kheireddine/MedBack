package tn.iteam.meduserservice.controllers.specs;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.meduserservice.dtos.requests.DoctorRequestDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IUserController {
    @GetMapping(ApiUtils.API_GET_ALL_USERS)
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @GetMapping(ApiUtils.API_GET_USER_BY_ID)
    ResponseEntity<UserResponseDto> getUserById(@PathVariable("userId") String userId);

    @PostMapping(ApiUtils.API_CREATE_DOCTOR)
    ResponseEntity<DoctorResponseDto> createDoctor(@Valid @RequestBody DoctorRequestDto requestDto);

    @GetMapping(ApiUtils.API_GET_ALL_DOCTORS)
    ResponseEntity<List<DoctorResponseDto>> getAllDoctors();

    @GetMapping(ApiUtils.API_GET_DOCTOR_BY_ID)
    ResponseEntity<DoctorResponseDto> getDoctorById(@PathVariable("doctorId") String doctorId);

    @PutMapping(ApiUtils.API_UPDATE_DOCTOR)
    ResponseEntity<DoctorResponseDto> updateDoctor(@PathVariable("doctorId") String doctorId,
                                                   @Valid @RequestBody DoctorRequestDto requestDto);

    @DeleteMapping(ApiUtils.API_DELETE_DOCTOR)
    ResponseEntity<Void> deleteDoctor(@PathVariable("doctorId") String doctorId);

    @GetMapping(ApiUtils.API_GET_ALL_PATIENTS)
    ResponseEntity<List<PatientResponseDto>> getAllPatients();

    @GetMapping(ApiUtils.API_GET_PATIENT_BY_ID)
    ResponseEntity<PatientResponseDto> getPatientById(@PathVariable("patientId") String patientId);

    @PatchMapping(ApiUtils.API_ARCHIVE_PATIENT)
    ResponseEntity<PatientResponseDto> archivePatient(@PathVariable("patientId") String patientId);
}
