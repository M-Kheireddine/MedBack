package tn.iteam.meduserservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;

@Mapper(componentModel = "spring")
public interface PatientDtoMapper {
    PatientDtoMapper INSTANCE = Mappers.getMapper(PatientDtoMapper.class);

    @Mapping(target = "maskedSocialSecurityNumber", expression = "java(maskSocialSecurityNumber(source.getSocialSecurityNumber()))")
    @Mapping(target = "totalAppointments", expression = "java(0L)")
    @Mapping(target = "totalPrescriptions", expression = "java(0L)")
    PatientDto toPatientDto(PatientResponseDto source);

    default String maskSocialSecurityNumber(String socialSecurityNumber) {
        if (socialSecurityNumber == null || socialSecurityNumber.isBlank()) {
            return null;
        }

        int visibleCharacters = Math.min(3, socialSecurityNumber.length());
        return "*".repeat(Math.max(0, socialSecurityNumber.length() - visibleCharacters))
                + socialSecurityNumber.substring(socialSecurityNumber.length() - visibleCharacters);
    }
}
