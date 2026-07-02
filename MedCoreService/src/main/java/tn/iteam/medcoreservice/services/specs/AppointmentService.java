package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.requests.AppointmentStatusUpdateRequestDto;
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
    private final PatientIdentifierResolver patientIdentifierResolver;

    @Override
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        String normalizedPatientId = patientIdentifierResolver.resolvePrimaryPatientId(requestDto.getPatientId());
        validateAppointmentAvailability(
                null,
                requestDto.getDoctorId(),
                normalizedPatientId,
                requestDto.getStartDateTime(),
                requestDto.getEndDateTime()
        );
        Appointment appointment = appointmentMapper.toAppointment(requestDto);
        appointment.setPatientId(normalizedPatientId);
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
    public List<AppointmentResponseDto> getDoctorAppointmentsInRange(String doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return appointmentRepository.findDoctorAppointmentsInRange(doctorId, startDateTime, endDateTime)
                .stream()
                .map(appointmentMapper::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByDoctorId(String doctorId) {
        return appointmentRepository.findByDoctorIdOrderByStartDateTimeAsc(doctorId)
                .stream()
                .map(appointmentMapper::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByPatientId(String patientId) {
        return appointmentRepository.findByPatientIdInOrderByStartDateTimeAsc(
                        patientIdentifierResolver.resolveCandidatePatientIds(patientId)
                )
                .stream()
                .map(appointmentMapper::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public AppointmentResponseDto updateAppointment(String appointmentId, AppointmentRequestDto requestDto) {
        Appointment appointment = findAppointmentById(appointmentId);
        String normalizedPatientId = patientIdentifierResolver.resolvePrimaryPatientId(requestDto.getPatientId());
        validateAppointmentAvailability(
                appointmentId,
                requestDto.getDoctorId(),
                normalizedPatientId,
                requestDto.getStartDateTime(),
                requestDto.getEndDateTime()
        );
        appointment.setDoctorId(requestDto.getDoctorId());
        appointment.setPatientId(normalizedPatientId);
        appointment.setStartDateTime(requestDto.getStartDateTime());
        appointment.setEndDateTime(requestDto.getEndDateTime());
        appointment.setReason(requestDto.getReason());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toAppointmentResponseDto(savedAppointment);
    }

    @Override
    public AppointmentResponseDto updateAppointmentStatus(String appointmentId, AppointmentStatusUpdateRequestDto requestDto) {
        Appointment appointment = findAppointmentById(appointmentId);

        if (requestDto.getStatus() == AppointmentStatus.SCHEDULED) {
            validateAppointmentAvailability(
                    appointmentId,
                    appointment.getDoctorId(),
                    appointment.getPatientId(),
                    appointment.getStartDateTime(),
                    appointment.getEndDateTime()
            );
        }

        appointment.setStatus(requestDto.getStatus());
        return appointmentMapper.toAppointmentResponseDto(appointmentRepository.save(appointment));
    }

    private Appointment findAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
    }

    private void validateAppointmentAvailability(
            String appointmentId,
            String doctorId,
            String patientId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        boolean doctorHasConflict = appointmentRepository
                .findByDoctorIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        doctorId,
                        AppointmentStatus.SCHEDULED,
                        endDateTime,
                        startDateTime
                )
                .stream()
                .anyMatch(appointment -> isDifferentAppointment(appointment, appointmentId));

        boolean patientHasConflict = appointmentRepository
                .findByPatientIdInAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                        patientIdentifierResolver.resolveCandidatePatientIds(patientId),
                        AppointmentStatus.SCHEDULED,
                        endDateTime,
                        startDateTime
                )
                .stream()
                .anyMatch(appointment -> isDifferentAppointment(appointment, appointmentId));

        if (doctorHasConflict || patientHasConflict) {
            throw new AppointmentConflictException("Doctor or patient already has a scheduled appointment in this time range.");
        }
    }

    private boolean isDifferentAppointment(Appointment appointment, String appointmentId) {
        return appointmentId == null || !appointment.getId().equals(appointmentId);
    }
}
