package tn.iteam.medcoreservice.services.impls;

import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;

import java.util.List;

public interface IPrescriptionService {
    PrescriptionResponseDto createPrescription(PrescriptionRequestDto requestDto);

    List<PrescriptionResponseDto> getAllPrescriptions();

    PrescriptionResponseDto getPrescriptionById(String prescriptionId);

    PrescriptionDto getPrescriptionDetails(String prescriptionId);

    List<PrescriptionResponseDto> getPrescriptionsByDoctorId(String doctorId);

    List<PrescriptionResponseDto> getPrescriptionsByPatientId(String patientId);

    PrescriptionDto updatePrescription(String prescriptionId, PrescriptionRequestDto requestDto);

    void deletePrescription(String prescriptionId);
}
