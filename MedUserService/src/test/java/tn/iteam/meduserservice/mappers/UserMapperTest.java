package tn.iteam.meduserservice.mappers;

import org.junit.jupiter.api.Test;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.models.DoctorEntity;
import tn.iteam.meduserservice.models.PatientEntity;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.models.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toUserResponseDtoShouldReturnNullWhenEntityIsNull() {
        assertNull(userMapper.toUserResponseDto(null));
    }

    @Test
    void toUserResponseDtoShouldMapCommonUserFields() {
        UUID userId = UUID.randomUUID();
        UserEntity entity = UserEntity.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@medback.com")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.of(2026, 7, 2, 13, 0))
                .isActive(Boolean.TRUE)
                .build();

        UserResponseDto responseDto = userMapper.toUserResponseDto(entity);

        assertEquals(userId, responseDto.getId());
        assertEquals(Role.ADMIN, responseDto.getRole());
    }

    @Test
    void toDoctorResponseDtoShouldMapDoctorFields() {
        DoctorEntity entity = DoctorEntity.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@medback.com")
                .role(Role.DOCTOR)
                .createdAt(LocalDateTime.of(2026, 7, 2, 13, 30))
                .isActive(Boolean.TRUE)
                .specialty("Cardiology")
                .phoneNumber("123")
                .clinicAddress("Clinic")
                .medicalLicenseNumber("LIC-9")
                .build();

        DoctorResponseDto responseDto = userMapper.toDoctorResponseDto(entity);

        assertEquals("Cardiology", responseDto.getSpecialty());
        assertEquals("LIC-9", responseDto.getMedicalLicenseNumber());
    }

    @Test
    void toPatientResponseDtoShouldMapPatientFields() {
        PatientEntity entity = PatientEntity.builder()
                .id(UUID.randomUUID())
                .firstName("Ariel")
                .lastName("Richardson")
                .email("ariel@medback.com")
                .role(Role.PATIENT)
                .createdAt(LocalDateTime.of(2026, 7, 2, 14, 0))
                .isActive(Boolean.TRUE)
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();

        PatientResponseDto responseDto = userMapper.toPatientResponseDto(entity);

        assertEquals("461", responseDto.getSocialSecurityNumber());
        assertEquals("B-", responseDto.getBloodType());
    }
}
