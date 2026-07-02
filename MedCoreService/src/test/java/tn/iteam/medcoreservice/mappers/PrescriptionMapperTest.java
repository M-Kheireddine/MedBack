package tn.iteam.medcoreservice.mappers;

import org.junit.jupiter.api.Test;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionLineRequestDto;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.models.PrescriptionLine;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrescriptionMapperTest {

    private final PrescriptionMapper prescriptionMapper = new PrescriptionMapper();

    @Test
    void toPrescriptionShouldReturnNullWhenRequestIsNull() {
        assertNull(prescriptionMapper.toPrescription(null));
    }

    @Test
    void toPrescriptionShouldMapRequestAndCreateLines() {
        PrescriptionRequestDto requestDto = PrescriptionRequestDto.builder()
                .doctorId("doctor-1")
                .patientId("patient-1")
                .doctorNotes("Doctor notes")
                .prescriptionLines(List.of(
                        PrescriptionLineRequestDto.builder()
                                .medicationId("med-1")
                                .dosage("1 tablet")
                                .duration("7 days")
                                .build()
                ))
                .build();

        Prescription prescription = prescriptionMapper.toPrescription(requestDto);

        assertEquals("doctor-1", prescription.getDoctorId());
        assertEquals("patient-1", prescription.getPatientId());
        assertEquals(1, prescription.getPrescriptionLines().size());
        assertNotNull(prescription.getCreatedAt());
    }

    @Test
    void toPrescriptionLineShouldReturnNullWhenRequestIsNull() {
        assertNull(prescriptionMapper.toPrescriptionLine(null));
    }

    @Test
    void toPrescriptionResponseDtoShouldReturnEmptyLinesWhenPrescriptionLinesAreNull() {
        Prescription prescription = Prescription.builder()
                .id("prescription-1")
                .doctorId("doctor-1")
                .patientId("patient-1")
                .createdAt(LocalDateTime.of(2026, 7, 2, 12, 0))
                .doctorNotes("Doctor notes")
                .prescriptionLines(null)
                .build();

        PrescriptionResponseDto responseDto = prescriptionMapper.toPrescriptionResponseDto(prescription);

        assertEquals("prescription-1", responseDto.getId());
        assertTrue(responseDto.getPrescriptionLines().isEmpty());
    }

    @Test
    void toPrescriptionResponseDtoShouldReturnNullWhenPrescriptionIsNull() {
        assertNull(prescriptionMapper.toPrescriptionResponseDto(null));
    }

    @Test
    void toPrescriptionResponseDtoShouldCopyPrescriptionLines() {
        Prescription prescription = Prescription.builder()
                .id("prescription-2")
                .doctorId("doctor-2")
                .patientId("patient-2")
                .createdAt(LocalDateTime.of(2026, 7, 2, 12, 30))
                .doctorNotes("Notes")
                .prescriptionLines(List.of(
                        PrescriptionLine.builder()
                                .medicationId("med-2")
                                .dosage("2 tablets")
                                .duration("3 days")
                                .build()
                ))
                .build();

        PrescriptionResponseDto responseDto = prescriptionMapper.toPrescriptionResponseDto(prescription);

        assertEquals(1, responseDto.getPrescriptionLines().size());
        assertEquals("med-2", responseDto.getPrescriptionLines().get(0).getMedicationId());
    }
}
