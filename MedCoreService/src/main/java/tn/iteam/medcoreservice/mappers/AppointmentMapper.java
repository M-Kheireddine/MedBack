package tn.iteam.medcoreservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.models.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    Appointment toAppointment(AppointmentRequestDto requestDto);

    AppointmentResponseDto toAppointmentResponseDto(Appointment appointment);
}
