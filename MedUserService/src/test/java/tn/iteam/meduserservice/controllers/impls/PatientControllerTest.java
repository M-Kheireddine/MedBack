package tn.iteam.meduserservice.controllers.impls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private PatientController patientController;

    @Test
    void getPatientProfileShouldReturnProfileFromService() {
        PatientDto expectedPatient = patientDto("PAT-100-200-300");
        when(userService.getPatientProfile("PAT-100-200-300")).thenReturn(expectedPatient);

        ResponseEntity<PatientDto> response = patientController.getPatientProfile("PAT-100-200-300");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedPatient, response.getBody());
        verify(userService).getPatientProfile("PAT-100-200-300");
    }

    @Test
    void getPatientSummaryShouldReturnSummaryFromService() {
        PatientDto expectedPatient = patientDto("PAT-111-222-333");
        when(userService.getPatientSummary("PAT-111-222-333")).thenReturn(expectedPatient);

        ResponseEntity<PatientDto> response = patientController.getPatientSummary("PAT-111-222-333");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedPatient, response.getBody());
        verify(userService).getPatientSummary("PAT-111-222-333");
    }

    @Test
    void unarchivePatientShouldReturnUpdatedPatient() {
        PatientDto expectedPatient = patientDto("PAT-444-555-666");
        when(userService.unarchivePatient("PAT-444-555-666")).thenReturn(expectedPatient);

        ResponseEntity<PatientDto> response = patientController.unarchivePatient("PAT-444-555-666");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedPatient, response.getBody());
        verify(userService).unarchivePatient("PAT-444-555-666");
    }

    private PatientDto patientDto(String functionalId) {
        return PatientDto.builder()
                .id(UUID.randomUUID())
                .firstName("Ariel")
                .lastName("Richardson")
                .email("patient@medback.com")
                .role(Role.PATIENT)
                .createdAt(LocalDateTime.of(2026, 7, 3, 9, 30))
                .isActive(Boolean.TRUE)
                .functionalId(functionalId)
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();
    }
}
