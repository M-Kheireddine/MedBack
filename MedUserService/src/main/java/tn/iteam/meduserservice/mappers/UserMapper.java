package tn.iteam.meduserservice.mappers;

import org.springframework.stereotype.Component;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.models.DoctorEntity;
import tn.iteam.meduserservice.models.PatientEntity;
import tn.iteam.meduserservice.models.UserEntity;

@Component
public class UserMapper {
    public UserResponseDto toUserResponseDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .build();
    }

    public DoctorResponseDto toDoctorResponseDto(DoctorEntity entity) {
        if (entity == null) {
            return null;
        }

        return DoctorResponseDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .specialty(entity.getSpecialty())
                .phoneNumber(entity.getPhoneNumber())
                .clinicAddress(entity.getClinicAddress())
                .medicalLicenseNumber(entity.getMedicalLicenseNumber())
                .build();
    }

    public PatientResponseDto toPatientResponseDto(PatientEntity entity) {
        if (entity == null) {
            return null;
        }

        return PatientResponseDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .birthDate(entity.getBirthDate())
                .socialSecurityNumber(entity.getSocialSecurityNumber())
                .bloodType(entity.getBloodType())
                .build();
    }
}
