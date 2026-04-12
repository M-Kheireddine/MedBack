package tn.iteam.medcoreservice.services.impls;

import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;

import java.util.List;

public interface IPrescriptionService {
    List<PrescriptionResponseDto> getAllPrescriptions();
    PrescriptionResponseDto getPrescriptionById(String prescriptionId);
}
