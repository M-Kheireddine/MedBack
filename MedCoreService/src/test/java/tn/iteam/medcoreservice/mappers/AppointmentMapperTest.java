package tn.iteam.medcoreservice.mappers;

import org.junit.jupiter.api.Test;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.AppointmentStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AppointmentMapperTest {

    private final AppointmentMapper appointmentMapper = new AppointmentMapper();

    @Test
    void toAppointmentShouldReturnNullWhenRequestIsNull() {
        assertNull(appointmentMapper.toAppointment(null));
    }

    @Test
    void toAppointmentShouldMapRequestToScheduledAppointment() {
        AppointmentRequestDto requestDto = AppointmentRequestDto.builder()
                .doctorId("doctor-1")
                .patientId("patient-1")
                .startDateTime(LocalDateTime.of(2026, 7, 3, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 7, 3, 10, 30))
                .reason("Consultation")
                .build();

        Appointment appointment = appointmentMapper.toAppointment(requestDto);

        assertEquals("doctor-1", appointment.getDoctorId());
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void toAppointmentResponseDtoShouldReturnNullWhenAppointmentIsNull() {
        assertNull(appointmentMapper.toAppointmentResponseDto(null));
    }

    @Test
    void toAppointmentResponseDtoShouldMapAppointmentFields() {
        Appointment appointment = Appointment.builder()
                .id("appointment-1")
                .doctorId("doctor-1")
                .patientId("patient-1")
                .startDateTime(LocalDateTime.of(2026, 7, 3, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 7, 3, 10, 30))
                .status(AppointmentStatus.COMPLETED)
                .reason("Consultation")
                .build();

        AppointmentResponseDto responseDto = appointmentMapper.toAppointmentResponseDto(appointment);

        assertEquals("appointment-1", responseDto.getId());
        assertEquals(AppointmentStatus.COMPLETED, responseDto.getStatus());
    }
}
