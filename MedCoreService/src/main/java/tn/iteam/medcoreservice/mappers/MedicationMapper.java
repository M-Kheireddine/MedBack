package tn.iteam.medcoreservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.models.Medication;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    @Mapping(target = "id", ignore = true)
    Medication toMedication(MedicationRequestDto requestDto);

    MedicationResponseDto toMedicationResponseDto(Medication medication);
}
