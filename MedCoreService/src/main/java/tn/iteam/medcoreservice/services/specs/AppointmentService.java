package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.clients.UserProfileClient;
import tn.iteam.medcoreservice.clients.dto.InternalDoctorProfileDto;
import tn.iteam.medcoreservice.clients.dto.InternalPatientProfileDto;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.requests.AppointmentStatusUpdateRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionDoctorMetadataDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionPatientMetadataDto;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final NotificationEventPublisher notificationEventPublisher;
    private final PatientIdentifierResolver patientIdentifierResolver;
    private final UserProfileClient userProfileClient;

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
        return toAppointmentResponseDto(savedAppointment);
    }

    @Override
    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public AppointmentResponseDto getAppointmentById(String appointmentId) {
        return toAppointmentResponseDto(findAppointmentById(appointmentId));
    }

    @Override
    public List<AppointmentResponseDto> getDoctorAppointmentsInRange(String doctorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return appointmentRepository.findDoctorAppointmentsInRange(doctorId, startDateTime, endDateTime)
                .stream()
                .map(this::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByDoctorId(String doctorId) {
        return appointmentRepository.findByDoctorIdOrderByStartDateTimeAsc(doctorId)
                .stream()
                .map(this::toAppointmentResponseDto)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> getAppointmentsByPatientId(String patientId) {
        return appointmentRepository.findByPatientIdInOrderByStartDateTimeAsc(
                        patientIdentifierResolver.resolveCandidatePatientIds(patientId)
                )
                .stream()
                .map(this::toAppointmentResponseDto)
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
        return toAppointmentResponseDto(savedAppointment);
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
        return toAppointmentResponseDto(appointmentRepository.save(appointment));
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

    private AppointmentResponseDto toAppointmentResponseDto(Appointment appointment) {
        AppointmentResponseDto responseDto = appointmentMapper.toAppointmentResponseDto(appointment);
        responseDto.setDoctor(resolveDoctorMetadata(appointment.getDoctorId()).orElse(null));
        responseDto.setPatient(resolvePatientMetadata(appointment.getPatientId()).orElse(null));
        return responseDto;
    }

    private Optional<PrescriptionDoctorMetadataDto> resolveDoctorMetadata(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            return Optional.empty();
        }

        try {
            InternalDoctorProfileDto doctorProfile = userProfileClient.getDoctorProfile(doctorId);
            return Optional.of(PrescriptionDoctorMetadataDto.builder()
                    .id(doctorProfile.getId())
                    .firstName(doctorProfile.getFirstName())
                    .lastName(doctorProfile.getLastName())
                    .email(doctorProfile.getEmail())
                    .specialty(doctorProfile.getSpecialty())
                    .phoneNumber(doctorProfile.getPhoneNumber())
                    .clinicAddress(doctorProfile.getClinicAddress())
                    .medicalLicenseNumber(doctorProfile.getMedicalLicenseNumber())
                    .profileImageUrl(doctorProfile.getProfileImageUrl())
                    .build());
        } catch (Exception exception) {
            log.warn("Unable to resolve doctor metadata for doctorId={}: {}", doctorId, exception.getMessage());
            return Optional.empty();
        }
    }

    private Optional<PrescriptionPatientMetadataDto> resolvePatientMetadata(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return Optional.empty();
        }

        try {
            InternalPatientProfileDto patientProfile = userProfileClient.getPatientProfile(patientId);
            return Optional.of(PrescriptionPatientMetadataDto.builder()
                    .id(patientProfile.getId())
                    .functionalId(patientProfile.getFunctionalId())
                    .firstName(patientProfile.getFirstName())
                    .lastName(patientProfile.getLastName())
                    .email(patientProfile.getEmail())
                    .birthDate(patientProfile.getBirthDate())
                    .socialSecurityNumber(patientProfile.getSocialSecurityNumber())
                    .bloodType(patientProfile.getBloodType())
                    .profileImageUrl(patientProfile.getProfileImageUrl())
                    .build());
        } catch (Exception exception) {
            log.warn("Unable to resolve patient metadata for patientId={}: {}", patientId, exception.getMessage());
            return Optional.empty();
        }
    }
}
