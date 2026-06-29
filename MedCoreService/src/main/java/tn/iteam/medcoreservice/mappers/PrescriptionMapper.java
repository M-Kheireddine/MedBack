package tn.iteam.medcoreservice.mappers;

import org.springframework.stereotype.Component;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionLineRequestDto;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.models.PrescriptionLine;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PrescriptionMapper {
    public Prescription toPrescription(PrescriptionRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return Prescription.builder()
                .doctorId(requestDto.getDoctorId())
                .patientId(requestDto.getPatientId())
                .createdAt(LocalDateTime.now())
                .doctorNotes(requestDto.getDoctorNotes())
                .prescriptionLines(toPrescriptionLines(requestDto.getPrescriptionLines()))
                .build();
    }

    public PrescriptionLine toPrescriptionLine(PrescriptionLineRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return PrescriptionLine.builder()
                .medicationId(requestDto.getMedicationId())
                .dosage(requestDto.getDosage())
                .duration(requestDto.getDuration())
                .build();
    }

    public PrescriptionResponseDto toPrescriptionResponseDto(Prescription prescription) {
        if (prescription == null) {
            return null;
        }

        return PrescriptionResponseDto.builder()
                .id(prescription.getId())
                .doctorId(prescription.getDoctorId())
                .patientId(prescription.getPatientId())
                .createdAt(prescription.getCreatedAt())
                .doctorNotes(prescription.getDoctorNotes())
                .prescriptionLines(copyPrescriptionLines(prescription.getPrescriptionLines()))
                .build();
    }

    private List<PrescriptionLine> toPrescriptionLines(List<PrescriptionLineRequestDto> requestDtos) {
        if (requestDtos == null) {
            return List.of();
        }

        return requestDtos.stream()
                .map(this::toPrescriptionLine)
                .toList();
    }

    private List<PrescriptionLine> copyPrescriptionLines(List<PrescriptionLine> prescriptionLines) {
        if (prescriptionLines == null) {
            return List.of();
        }

        return prescriptionLines.stream()
                .map(prescriptionLine -> PrescriptionLine.builder()
                        .medicationId(prescriptionLine.getMedicationId())
                        .dosage(prescriptionLine.getDosage())
                        .duration(prescriptionLine.getDuration())
                        .build())
                .toList();
    }
}
