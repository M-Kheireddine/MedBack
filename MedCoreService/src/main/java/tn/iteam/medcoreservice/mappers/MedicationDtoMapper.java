package tn.iteam.medcoreservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tn.iteam.medcoreservice.dtos.responses.MedicationDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;

@Mapper(componentModel = "spring")
public interface MedicationDtoMapper {
    MedicationDtoMapper INSTANCE = Mappers.getMapper(MedicationDtoMapper.class);

    MedicationDto toMedicationDto(MedicationResponseDto source);
}
