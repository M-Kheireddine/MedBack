package tn.iteam.medcoreservice.services.impls;

import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.requests.AppointmentStatusUpdateRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {
    AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto);

    List<AppointmentResponseDto> getAllAppointments();

    AppointmentResponseDto getAppointmentById(String appointmentId);

    List<AppointmentResponseDto> getDoctorAppointmentsInRange(String doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AppointmentResponseDto> getAppointmentsByDoctorId(String doctorId);

    List<AppointmentResponseDto> getAppointmentsByPatientId(String patientId);

    AppointmentResponseDto updateAppointment(String appointmentId, AppointmentRequestDto requestDto);

    AppointmentResponseDto updateAppointmentStatus(String appointmentId, AppointmentStatusUpdateRequestDto requestDto);
}
