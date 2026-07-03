package tn.iteam.medcoreservice.services.specs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.medcoreservice.clients.UserProfileClient;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    @Mock
    private PatientIdentifierResolver patientIdentifierResolver;

    @Mock
    private UserProfileClient userProfileClient;

    private final AppointmentMapper appointmentMapper = new AppointmentMapper();

    private AppointmentService buildService() {
        return new AppointmentService(
                appointmentRepository,
                appointmentMapper,
                notificationEventPublisher,
                patientIdentifierResolver,
                userProfileClient
        );
    }

    @Test
    void createAppointmentShouldSaveScheduledAppointmentAndPublishEvent() {
        AppointmentService appointmentService = buildService();

        AppointmentRequestDto requestDto = appointmentRequest(
                "doctor-1",
                "patient-1",
                LocalDateTime.of(2026, 7, 3, 10, 0),
                LocalDateTime.of(2026, 7, 3, 10, 30)
        );
        stubPrimaryPatientIdentifierResolution();
        stubCandidatePatientIdentifierResolution();

        when(appointmentRepository.findByDoctorIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq("doctor-1"), eq(AppointmentStatus.SCHEDULED), eq(requestDto.getEndDateTime()), eq(requestDto.getStartDateTime())
        )).thenReturn(List.of());
        when(appointmentRepository.findByPatientIdInAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(List.of("patient-1")), eq(AppointmentStatus.SCHEDULED), eq(requestDto.getEndDateTime()), eq(requestDto.getStartDateTime())
        )).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId("appointment-1");
            return appointment;
        });

        AppointmentResponseDto response = appointmentService.createAppointment(requestDto);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment savedAppointment = appointmentCaptor.getValue();

        assertEquals("doctor-1", savedAppointment.getDoctorId());
        assertEquals("patient-1", savedAppointment.getPatientId());
        assertEquals(AppointmentStatus.SCHEDULED, savedAppointment.getStatus());
        verify(notificationEventPublisher).publishAppointmentCreated(savedAppointment, "recipient@example.com");

        assertEquals("appointment-1", response.getId());
        assertEquals(AppointmentStatus.SCHEDULED, response.getStatus());
    }

    @Test
    void createAppointmentShouldThrowWhenDoctorHasConflict() {
        AppointmentService appointmentService = buildService();

        AppointmentRequestDto requestDto = appointmentRequest(
                "doctor-2",
                "patient-2",
                LocalDateTime.of(2026, 7, 3, 11, 0),
                LocalDateTime.of(2026, 7, 3, 11, 30)
        );
        stubPrimaryPatientIdentifierResolution();
        stubCandidatePatientIdentifierResolution();

        when(appointmentRepository.findByDoctorIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq("doctor-2"), eq(AppointmentStatus.SCHEDULED), eq(requestDto.getEndDateTime()), eq(requestDto.getStartDateTime())
        )).thenReturn(List.of(appointment("appointment-conflict", "doctor-2", "patient-x", requestDto.getStartDateTime(), requestDto.getEndDateTime(), AppointmentStatus.SCHEDULED)));
        when(appointmentRepository.findByPatientIdInAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(List.of("patient-2")), eq(AppointmentStatus.SCHEDULED), eq(requestDto.getEndDateTime()), eq(requestDto.getStartDateTime())
        )).thenReturn(List.of());

        AppointmentConflictException exception = assertThrows(
                AppointmentConflictException.class,
                () -> appointmentService.createAppointment(requestDto)
        );

        assertEquals("Doctor or patient already has a scheduled appointment in this time range.", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateAppointmentShouldIgnoreCurrentAppointmentDuringConflictValidation() {
        AppointmentService appointmentService = buildService();

        Appointment existingAppointment = appointment("appointment-3", "doctor-3", "patient-3",
                LocalDateTime.of(2026, 7, 3, 12, 0),
                LocalDateTime.of(2026, 7, 3, 12, 30),
                AppointmentStatus.SCHEDULED);
        AppointmentRequestDto requestDto = appointmentRequest(
                "doctor-3",
                "patient-3",
                LocalDateTime.of(2026, 7, 3, 13, 0),
                LocalDateTime.of(2026, 7, 3, 13, 30)
        );
        stubPrimaryPatientIdentifierResolution();
        stubCandidatePatientIdentifierResolution();

        when(appointmentRepository.findById("appointment-3")).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.findByDoctorIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq("doctor-3"), eq(AppointmentStatus.SCHEDULED), eq(requestDto.getEndDateTime()), eq(requestDto.getStartDateTime())
        )).thenReturn(List.of(appointment("appointment-3", "doctor-3", "patient-3", requestDto.getStartDateTime(), requestDto.getEndDateTime(), AppointmentStatus.SCHEDULED)));
        when(appointmentRepository.findByPatientIdInAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(List.of("patient-3")), eq(AppointmentStatus.SCHEDULED), eq(requestDto.getEndDateTime()), eq(requestDto.getStartDateTime())
        )).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponseDto response = appointmentService.updateAppointment("appointment-3", requestDto);

        assertEquals(LocalDateTime.of(2026, 7, 3, 13, 0), response.getStartDateTime());
        assertEquals(LocalDateTime.of(2026, 7, 3, 13, 30), response.getEndDateTime());
        assertEquals("General consultation", response.getReason());
    }

    @Test
    void updateAppointmentStatusShouldValidateAvailabilityWhenStatusIsScheduled() {
        AppointmentService appointmentService = buildService();

        Appointment existingAppointment = appointment("appointment-4", "doctor-4", "patient-4",
                LocalDateTime.of(2026, 7, 3, 14, 0),
                LocalDateTime.of(2026, 7, 3, 14, 30),
                AppointmentStatus.CANCELED);
        AppointmentStatusUpdateRequestDto requestDto = AppointmentStatusUpdateRequestDto.builder()
                .status(AppointmentStatus.SCHEDULED)
                .build();
        stubCandidatePatientIdentifierResolution();

        when(appointmentRepository.findById("appointment-4")).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.findByDoctorIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq("doctor-4"), eq(AppointmentStatus.SCHEDULED), eq(existingAppointment.getEndDateTime()), eq(existingAppointment.getStartDateTime())
        )).thenReturn(List.of());
        when(appointmentRepository.findByPatientIdInAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(
                eq(List.of("patient-4")), eq(AppointmentStatus.SCHEDULED), eq(existingAppointment.getEndDateTime()), eq(existingAppointment.getStartDateTime())
        )).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponseDto response = appointmentService.updateAppointmentStatus("appointment-4", requestDto);

        assertEquals(AppointmentStatus.SCHEDULED, response.getStatus());
        verify(appointmentRepository).save(existingAppointment);
    }

    @Test
    void updateAppointmentStatusShouldNotValidateAvailabilityWhenStatusIsCompleted() {
        AppointmentService appointmentService = buildService();

        Appointment existingAppointment = appointment("appointment-5", "doctor-5", "patient-5",
                LocalDateTime.of(2026, 7, 3, 15, 0),
                LocalDateTime.of(2026, 7, 3, 15, 30),
                AppointmentStatus.SCHEDULED);
        AppointmentStatusUpdateRequestDto requestDto = AppointmentStatusUpdateRequestDto.builder()
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(appointmentRepository.findById("appointment-5")).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponseDto response = appointmentService.updateAppointmentStatus("appointment-5", requestDto);

        assertEquals(AppointmentStatus.COMPLETED, response.getStatus());
        verify(appointmentRepository, never()).findByDoctorIdAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(any(), any(), any(), any());
        verify(appointmentRepository, never()).findByPatientIdInAndStatusAndStartDateTimeLessThanAndEndDateTimeGreaterThan(anyList(), any(), any(), any());
    }

    @Test
    void getAppointmentByIdShouldThrowWhenAppointmentDoesNotExist() {
        AppointmentService appointmentService = buildService();

        when(appointmentRepository.findById("missing-appointment")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentService.getAppointmentById("missing-appointment")
        );

        assertEquals("Appointment not found with id: missing-appointment", exception.getMessage());
    }

    @Test
    void getDoctorAppointmentsInRangeShouldReturnMappedResults() {
        AppointmentService appointmentService = buildService();

        LocalDateTime start = LocalDateTime.of(2026, 7, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3, 17, 0);
        when(appointmentRepository.findDoctorAppointmentsInRange("doctor-6", start, end))
                .thenReturn(List.of(appointment("appointment-6", "doctor-6", "patient-6", start, end, AppointmentStatus.SCHEDULED)));

        List<AppointmentResponseDto> response = appointmentService.getDoctorAppointmentsInRange("doctor-6", start, end);

        assertEquals(1, response.size());
        assertEquals("doctor-6", response.get(0).getDoctorId());
    }

    private AppointmentRequestDto appointmentRequest(
            String doctorId,
            String patientId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        return AppointmentRequestDto.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .reason("General consultation")
                .recipientEmail("recipient@example.com")
                .build();
    }

    private void stubPrimaryPatientIdentifierResolution() {
        when(patientIdentifierResolver.resolvePrimaryPatientId(any()))
                .thenAnswer(invocation -> invocation.getArgument(0, String.class));
    }

    private void stubCandidatePatientIdentifierResolution() {
        when(patientIdentifierResolver.resolveCandidatePatientIds(any()))
                .thenAnswer(invocation -> List.of(invocation.getArgument(0, String.class)));
    }

    private Appointment appointment(
            String id,
            String doctorId,
            String patientId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            AppointmentStatus status
    ) {
        return Appointment.builder()
                .id(id)
                .doctorId(doctorId)
                .patientId(patientId)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .status(status)
                .reason("General consultation")
                .build();
    }
}
