package tn.iteam.medcoreservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionLineRequestDto;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.models.Prescription;
import tn.iteam.medcoreservice.models.PrescriptionLine;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Prescription toPrescription(PrescriptionRequestDto requestDto);

    PrescriptionLine toPrescriptionLine(PrescriptionLineRequestDto requestDto);

    PrescriptionResponseDto toPrescriptionResponseDto(Prescription prescription);
}
