package tn.iteam.meduserservice.controllers.specs;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.utils.ApiUtils;

@RequestMapping
public interface IProfileController {
    @GetMapping(ApiUtils.API_PROFILE_GET_DOCTOR)
    ResponseEntity<DoctorDto> getDoctorProfileDetails(@PathVariable("doctorId") String doctorId);

    @GetMapping(ApiUtils.API_PROFILE_GET_PATIENT)
    ResponseEntity<PatientDto> getPatientProfileDetails(@PathVariable("patientId") String patientId);

    @GetMapping(ApiUtils.API_INTERNAL_GET_DOCTOR_PROFILE)
    ResponseEntity<DoctorDto> getInternalDoctorProfile(@PathVariable("doctorId") String doctorId);

    @GetMapping(ApiUtils.API_INTERNAL_GET_PATIENT_PROFILE)
    ResponseEntity<PatientDto> getInternalPatientProfile(@PathVariable("patientId") String patientId);

    @PostMapping(value = ApiUtils.API_UPLOAD_DOCTOR_PROFILE_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DoctorDto> uploadDoctorProfileImage(@PathVariable("doctorId") String doctorId,
                                                       @RequestPart("image") MultipartFile image);

    @PostMapping(value = ApiUtils.API_UPLOAD_PATIENT_PROFILE_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PatientDto> uploadPatientProfileImage(@PathVariable("patientId") String patientId,
                                                         @RequestPart("image") MultipartFile image);
}
