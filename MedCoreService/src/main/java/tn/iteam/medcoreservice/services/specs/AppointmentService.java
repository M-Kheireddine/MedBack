package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.exceptions.AppointmentConflictException;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.mappers.AppointmentMapper;
import tn.iteam.medcoreservice.messaging.NotificationEventPublisher;
import tn.iteam.medcoreservice.models.Appointment;
import tn.iteam.medcoreservice.models.AppointmentStatus;
import tn.iteam.medcoreservice.repositories.AppointmentRepository;
import tn.iteam.medcoreservice.services.impls.IAppointmentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final NotificationEventPublisher notificationEventPublisher;

    @Override
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        validateAppointmentAvailability(null, requestDto.getDoctorId(), requestDto.getPatientId(), requestDto.getDateTime());
        Appointment appointment = appointmentMapper.toAppointment(requestDto);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        notificationEventPublisher.publishAppointmentCreated(savedAppointment, requestDto.getRecipientEmail());
        return appointmentMapper.toAppointmentResponseDto(savedAppointment);
    }

    @Override
    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public AppointmentResponseDto getAppointmentById(String appointmentId) {
        return appointmentMapper.toAppointmentResponseDto(findAppointmentById(appointmentId));
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByDoctorId(String doctorId) {
        return appointmentRepository.findByDoctorIdOrderByDateTimeAsc(doctorId)
                .stream()
                .map(appointmentMapper::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByPatientId(String patientId) {
        return appointmentRepository.findByPatientIdOrderByDateTimeAsc(patientId)
                .stream()
                .map(appointmentMapper::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public AppointmentResponseDto updateAppointment(String appointmentId, AppointmentRequestDto requestDto) {
        Appointment appointment = findAppointmentById(appointmentId);
        validateAppointmentAvailability(appointmentId, requestDto.getDoctorId(), requestDto.getPatientId(), requestDto.getDateTime());
        appointment.setDoctorId(requestDto.getDoctorId());
        appointment.setPatientId(requestDto.getPatientId());
        appointment.setDateTime(requestDto.getDateTime());
        appointment.setReason(requestDto.getReason());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toAppointmentResponseDto(savedAppointment);
    }

    @Override
    public AppointmentResponseDto cancelAppointment(String appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELED);
        return appointmentMapper.toAppointmentResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentResponseDto completeAppointment(String appointmentId) {
        Appointment appointment = findAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentMapper.toAppointmentResponseDto(appointmentRepository.save(appointment));
    }

    private Appointment findAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
    }

    private void validateAppointmentAvailability(String appointmentId, String doctorId, String patientId, LocalDateTime dateTime) {
        boolean doctorHasConflict = appointmentRepository.findByDoctorIdAndDateTimeAndStatus(doctorId, dateTime, AppointmentStatus.SCHEDULED)
                .stream()
                .anyMatch(appointment -> isDifferentAppointment(appointment, appointmentId));
        boolean patientHasConflict = appointmentRepository.findByPatientIdAndDateTimeAndStatus(patientId, dateTime, AppointmentStatus.SCHEDULED)
                .stream()
                .anyMatch(appointment -> isDifferentAppointment(appointment, appointmentId));

        if (doctorHasConflict || patientHasConflict) {
            throw new AppointmentConflictException("Doctor or patient already has a scheduled appointment at this date time.");
        }
    }

    private boolean isDifferentAppointment(Appointment appointment, String appointmentId) {
        return appointmentId == null || !appointment.getId().equals(appointmentId);
    }
}
