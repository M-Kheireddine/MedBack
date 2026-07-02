package tn.iteam.meduserservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;

@Mapper(componentModel = "spring")
public interface DoctorDtoMapper {
    DoctorDtoMapper INSTANCE = Mappers.getMapper(DoctorDtoMapper.class);

    @Mapping(target = "totalAppointments", expression = "java(0L)")
    @Mapping(target = "totalPrescriptions", expression = "java(0L)")
    @Mapping(target = "activePatientsCount", expression = "java(0L)")
    DoctorDto toDoctorDto(DoctorResponseDto source);
}
