package tn.iteam.meduserservice.controllers.specs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.utils.ApiUtils;

@RequestMapping
public interface IPatientController {
    @GetMapping(ApiUtils.API_GET_PATIENT_PROFILE)
    ResponseEntity<PatientDto> getPatientProfile(@PathVariable("patientId") String patientId);

    @GetMapping(ApiUtils.API_GET_PATIENT_SUMMARY)
    ResponseEntity<PatientDto> getPatientSummary(@PathVariable("patientId") String patientId);

    @PatchMapping(ApiUtils.API_UNARCHIVE_PATIENT)
    ResponseEntity<PatientDto> unarchivePatient(@PathVariable("patientId") String patientId);
}
