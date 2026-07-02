package tn.iteam.meduserservice.controllers.specs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IDoctorController {
    @GetMapping(ApiUtils.API_PUBLIC_GET_ALL_DOCTORS)
    ResponseEntity<List<DoctorDto>> getPublicDoctors(@RequestParam(value = "search", required = false) String search,
                                                     @RequestParam(value = "specialty", required = false) String specialty);

    @GetMapping(ApiUtils.API_PUBLIC_GET_DOCTOR_BY_ID)
    ResponseEntity<DoctorDto> getPublicDoctorById(@PathVariable("doctorId") String doctorId);

    @GetMapping(ApiUtils.API_ADMIN_GET_DOCTOR_PROFILE)
    ResponseEntity<DoctorDto> getDoctorProfile(@PathVariable("doctorId") String doctorId);

    @GetMapping(ApiUtils.API_ADMIN_GET_DOCTOR_SUMMARY)
    ResponseEntity<DoctorDto> getDoctorSummary(@PathVariable("doctorId") String doctorId);
}
