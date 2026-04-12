package tn.iteam.medcoreservice.mappers;

import org.mapstruct.Mapper;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.models.PrescriptionEntity;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {
    PrescriptionResponseDto toPrescriptionResponseDto(PrescriptionEntity entity);
}
