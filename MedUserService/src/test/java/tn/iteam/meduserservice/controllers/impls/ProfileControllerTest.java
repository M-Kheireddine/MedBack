package tn.iteam.meduserservice.controllers.impls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void getDoctorProfileDetailsShouldReturnDoctorProfile() {
        DoctorDto expectedDoctor = doctorDto();
        when(userService.getDoctorProfile("doctor-1")).thenReturn(expectedDoctor);

        ResponseEntity<DoctorDto> response = profileController.getDoctorProfileDetails("doctor-1");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedDoctor, response.getBody());
        verify(userService).getDoctorProfile("doctor-1");
    }

    @Test
    void getPatientProfileDetailsShouldReturnPatientProfile() {
        PatientDto expectedPatient = patientDto();
        when(userService.getPatientProfile("PAT-123-456-789")).thenReturn(expectedPatient);

        ResponseEntity<PatientDto> response = profileController.getPatientProfileDetails("PAT-123-456-789");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedPatient, response.getBody());
        verify(userService).getPatientProfile("PAT-123-456-789");
    }

    @Test
    void getInternalDoctorProfileShouldReturnDoctorProfile() {
        DoctorDto expectedDoctor = doctorDto();
        when(userService.getDoctorProfile("doctor-2")).thenReturn(expectedDoctor);

        ResponseEntity<DoctorDto> response = profileController.getInternalDoctorProfile("doctor-2");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedDoctor, response.getBody());
        verify(userService).getDoctorProfile("doctor-2");
    }

    @Test
    void getInternalPatientProfileShouldReturnPatientProfile() {
        PatientDto expectedPatient = patientDto();
        when(userService.getPatientProfile("PAT-123-456-789")).thenReturn(expectedPatient);

        ResponseEntity<PatientDto> response = profileController.getInternalPatientProfile("PAT-123-456-789");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedPatient, response.getBody());
        verify(userService).getPatientProfile("PAT-123-456-789");
    }

    @Test
    void uploadDoctorProfileImageShouldDelegateToUserService() {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "doctor.png",
                "image/png",
                "doctor-image".getBytes(StandardCharsets.UTF_8)
        );
        DoctorDto expectedDoctor = doctorDto();
        when(userService.updateDoctorProfileImage(eq("doctor-1"), eq(image))).thenReturn(expectedDoctor);

        ResponseEntity<DoctorDto> response = profileController.uploadDoctorProfileImage("doctor-1", image);

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedDoctor, response.getBody());
        verify(userService).updateDoctorProfileImage("doctor-1", image);
    }

    @Test
    void uploadPatientProfileImageShouldDelegateToUserService() {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "patient.png",
                "image/png",
                "patient-image".getBytes(StandardCharsets.UTF_8)
        );
        PatientDto expectedPatient = patientDto();
        when(userService.updatePatientProfileImage(eq("PAT-123-456-789"), eq(image))).thenReturn(expectedPatient);

        ResponseEntity<PatientDto> response = profileController.uploadPatientProfileImage("PAT-123-456-789", image);

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedPatient, response.getBody());
        verify(userService).updatePatientProfileImage("PAT-123-456-789", image);
    }

    private DoctorDto doctorDto() {
        return DoctorDto.builder()
                .id(UUID.randomUUID())
                .firstName("Cyrine")
                .lastName("Srairi")
                .email("doctor@medback.com")
                .role(Role.DOCTOR)
                .createdAt(LocalDateTime.of(2026, 7, 3, 11, 0))
                .isActive(Boolean.TRUE)
                .specialty("Dermatology")
                .phoneNumber("5551234")
                .clinicAddress("Tunis")
                .medicalLicenseNumber("LIC-55")
                .build();
    }

    private PatientDto patientDto() {
        return PatientDto.builder()
                .id(UUID.randomUUID())
                .firstName("Ahmed")
                .lastName("Mouhamed")
                .email("patient@medback.com")
                .role(Role.PATIENT)
                .createdAt(LocalDateTime.of(2026, 7, 3, 11, 15))
                .isActive(Boolean.TRUE)
                .functionalId("PAT-123-456-789")
                .birthDate(LocalDate.of(1990, 4, 10))
                .socialSecurityNumber("999")
                .bloodType("A+")
                .build();
    }
}
