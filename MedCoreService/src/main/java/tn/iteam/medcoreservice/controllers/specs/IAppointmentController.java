package tn.iteam.medcoreservice.controllers.specs;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IAppointmentController {
    @PostMapping(ApiUtils.API_CREATE_APPOINTMENT)
    ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody AppointmentRequestDto requestDto);

    @GetMapping(ApiUtils.API_GET_ALL_APPOINTMENTS)
    ResponseEntity<List<AppointmentResponseDto>> getAllAppointments();

    @GetMapping(ApiUtils.API_GET_APPOINTMENT_BY_ID)
    ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable("appointmentId") String appointmentId);

    @GetMapping(ApiUtils.API_GET_APPOINTMENTS_BY_DOCTOR)
    ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDoctorId(@PathVariable("doctorId") String doctorId);

    @GetMapping(ApiUtils.API_GET_APPOINTMENTS_BY_PATIENT)
    ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPatientId(@PathVariable("patientId") String patientId);

    @PutMapping(ApiUtils.API_UPDATE_APPOINTMENT)
    ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable("appointmentId") String appointmentId,
                                                             @Valid @RequestBody AppointmentRequestDto requestDto);

    @PatchMapping(ApiUtils.API_CANCEL_APPOINTMENT)
    ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable("appointmentId") String appointmentId);

    @PatchMapping(ApiUtils.API_COMPLETE_APPOINTMENT)
    ResponseEntity<AppointmentResponseDto> completeAppointment(@PathVariable("appointmentId") String appointmentId);
}
