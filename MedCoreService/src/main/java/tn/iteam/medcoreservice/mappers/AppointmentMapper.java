package tn.iteam.medcoreservice.mappers;

import org.springframework.stereotype.Component;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.AppointmentStatus;

@Component
public class AppointmentMapper {
    public Appointment toAppointment(AppointmentRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return Appointment.builder()
                .doctorId(requestDto.getDoctorId())
                .patientId(requestDto.getPatientId())
                .startDateTime(requestDto.getStartDateTime())
                .endDateTime(requestDto.getEndDateTime())
                .status(AppointmentStatus.SCHEDULED)
                .reason(requestDto.getReason())
                .build();
    }

    public AppointmentResponseDto toAppointmentResponseDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctorId())
                .patientId(appointment.getPatientId())
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .build();
    }
}
