package tn.iteam.meduserservice.mappers;

import org.springframework.stereotype.Component;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.models.DoctorEntity;
import tn.iteam.meduserservice.models.PatientEntity;
import tn.iteam.meduserservice.models.UserEntity;
import tn.iteam.meduserservice.utils.ApiUtils;

import java.util.UUID;

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
                .profileImageUrl(resolveProfileImageUrl(entity.getId(), entity.getProfileImageBase64()))
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
                .profileImageUrl(resolveProfileImageUrl(entity.getId(), entity.getProfileImageBase64()))
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
                .profileImageUrl(resolveProfileImageUrl(entity.getId(), entity.getProfileImageBase64()))
                .birthDate(entity.getBirthDate())
                .socialSecurityNumber(entity.getSocialSecurityNumber())
                .bloodType(entity.getBloodType())
                .build();
    }

    private String resolveProfileImageUrl(UUID userId, String profileImageBase64) {
        if (userId == null || profileImageBase64 == null || profileImageBase64.isBlank()) {
            return null;
        }

        return ApiUtils.API_GET_PROFILE_IMAGE.replace("{userId}", userId.toString());
    }
}
