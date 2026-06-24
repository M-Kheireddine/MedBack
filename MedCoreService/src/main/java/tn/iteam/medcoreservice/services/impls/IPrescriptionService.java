package tn.iteam.medcoreservice.services.impls;

import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;

import java.util.List;

public interface IPrescriptionService {
    PrescriptionResponseDto createPrescription(PrescriptionRequestDto requestDto);

    List<PrescriptionResponseDto> getAllPrescriptions();

    PrescriptionResponseDto getPrescriptionById(String prescriptionId);

    List<PrescriptionResponseDto> getPrescriptionsByDoctorId(String doctorId);

    List<PrescriptionResponseDto> getPrescriptionsByPatientId(String patientId);

    void deletePrescription(String prescriptionId);
}
