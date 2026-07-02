package tn.iteam.medcoreservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionLineDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.models.PrescriptionLine;

@Mapper(componentModel = "spring")
public interface PrescriptionDtoMapper {
    PrescriptionDtoMapper INSTANCE = Mappers.getMapper(PrescriptionDtoMapper.class);

    PrescriptionDto toPrescriptionDto(PrescriptionResponseDto source);

    @Mapping(target = "medicationName", ignore = true)
    PrescriptionLineDto toPrescriptionLineDto(PrescriptionLine source);
}
