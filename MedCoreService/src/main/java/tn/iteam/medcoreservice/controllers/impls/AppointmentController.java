package tn.iteam.medcoreservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.medcoreservice.controllers.specs.IAppointmentController;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.requests.AppointmentStatusUpdateRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.services.impls.IAppointmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppointmentController implements IAppointmentController {
    private final IAppointmentService appointmentService;

    @Override
    public ResponseEntity<AppointmentResponseDto> createAppointment(AppointmentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(requestDto));
    }

    @Override
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(String appointmentId) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(appointmentId));
    }

    @Override
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDoctorId(
            String doctorId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        if (startDateTime == null && endDateTime == null) {
            return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
        }

        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("Both start and end query parameters must be provided together.");
        }

        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsInRange(doctorId, startDateTime, endDateTime));
    }

    @Override
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPatientId(String patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientId));
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> updateAppointment(String appointmentId, AppointmentRequestDto requestDto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(appointmentId, requestDto));
    }

    @Override
    public ResponseEntity<AppointmentResponseDto> updateAppointmentStatus(
            String appointmentId,
            AppointmentStatusUpdateRequestDto requestDto
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(appointmentId, requestDto));
    }
}
