package tn.iteam.medcoreservice.controllers.specs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IPrescriptionController {
    @GetMapping(ApiUtils.API_GET_ALL_PRESCRIPTIONS)
    ResponseEntity<List<PrescriptionResponseDto>> getAlPrescriptions();

    @GetMapping(ApiUtils.API_GET_PRESCRIPTION_BY_ID)
    ResponseEntity<PrescriptionResponseDto> getPrescriptionById(@PathVariable("prescriptionId") String prescriptionId);
}
