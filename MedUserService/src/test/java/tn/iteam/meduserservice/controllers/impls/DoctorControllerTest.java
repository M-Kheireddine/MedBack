package tn.iteam.meduserservice.controllers.impls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private DoctorController doctorController;

    @Test
    void getPublicDoctorsShouldDelegateToUserService() {
        List<DoctorDto> expectedDoctors = List.of(doctorDto("doctor-1@medback.com"), doctorDto("doctor-2@medback.com"));
        when(userService.searchPublicDoctors("cardio", "Cardiology")).thenReturn(expectedDoctors);

        ResponseEntity<List<DoctorDto>> response = doctorController.getPublicDoctors("cardio", "Cardiology");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedDoctors, response.getBody());
        verify(userService).searchPublicDoctors("cardio", "Cardiology");
    }

    @Test
    void getPublicDoctorByIdShouldReturnDoctorProfile() {
        DoctorDto expectedDoctor = doctorDto("doctor@medback.com");
        when(userService.getDoctorProfile("doctor-1")).thenReturn(expectedDoctor);

        ResponseEntity<DoctorDto> response = doctorController.getPublicDoctorById("doctor-1");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedDoctor, response.getBody());
        verify(userService).getDoctorProfile("doctor-1");
    }

    @Test
    void getDoctorProfileShouldReturnAdministrativeDoctorProfile() {
        DoctorDto expectedDoctor = doctorDto("admin.doctor@medback.com");
        when(userService.getDoctorProfile("doctor-3")).thenReturn(expectedDoctor);

        ResponseEntity<DoctorDto> response = doctorController.getDoctorProfile("doctor-3");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedDoctor, response.getBody());
        verify(userService).getDoctorProfile("doctor-3");
    }

    @Test
    void getDoctorSummaryShouldReturnSummaryFromUserService() {
        DoctorDto expectedDoctor = doctorDto("doctor.summary@medback.com");
        when(userService.getDoctorSummary("doctor-2")).thenReturn(expectedDoctor);

        ResponseEntity<DoctorDto> response = doctorController.getDoctorSummary("doctor-2");

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedDoctor, response.getBody());
        verify(userService).getDoctorSummary("doctor-2");
    }

    private DoctorDto doctorDto(String email) {
        return DoctorDto.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .role(Role.DOCTOR)
                .createdAt(LocalDateTime.of(2026, 7, 3, 10, 0))
                .isActive(Boolean.TRUE)
                .specialty("Cardiology")
                .phoneNumber("123456789")
                .clinicAddress("Clinic Address")
                .medicalLicenseNumber("LIC-1")
                .build();
    }
}
