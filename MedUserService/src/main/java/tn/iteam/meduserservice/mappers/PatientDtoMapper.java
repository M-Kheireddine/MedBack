package tn.iteam.meduserservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;

@Mapper(componentModel = "spring")
public interface PatientDtoMapper {
    PatientDtoMapper INSTANCE = Mappers.getMapper(PatientDtoMapper.class);

    @Mapping(target = "totalAppointments", expression = "java(0L)")
    @Mapping(target = "totalPrescriptions", expression = "java(0L)")
    PatientDto toPatientDto(PatientResponseDto source);
}
