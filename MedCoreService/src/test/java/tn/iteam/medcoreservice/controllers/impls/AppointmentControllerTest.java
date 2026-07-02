package tn.iteam.medcoreservice.controllers.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.iteam.medcoreservice.dtos.requests.AppointmentRequestDto;
import tn.iteam.medcoreservice.dtos.requests.AppointmentStatusUpdateRequestDto;
import tn.iteam.medcoreservice.dtos.responses.AppointmentResponseDto;
import tn.iteam.medcoreservice.exceptions.GlobalExceptionHandler;
import tn.iteam.medcoreservice.services.impls.IAppointmentService;
import tn.iteam.medcoreservice.models.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_CREATE_APPOINTMENT;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_GET_APPOINTMENTS_BY_DOCTOR;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_UPDATE_APPOINTMENT_STATUS;

@WebMvcTest(AppointmentController.class)
@Import(GlobalExceptionHandler.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAppointmentService appointmentService;

    @Test
    void createAppointmentShouldReturn201WhenPayloadIsValid() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(30);
        AppointmentRequestDto requestDto = validRequest(
                start,
                end
        );
        AppointmentResponseDto responseDto = appointmentResponse("appointment-1", AppointmentStatus.SCHEDULED);

        when(appointmentService.createAppointment(any(AppointmentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_CREATE_APPOINTMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("appointment-1"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(appointmentService).createAppointment(any(AppointmentRequestDto.class));
    }

    @Test
    void createAppointmentShouldReturn400WhenPayloadIsInvalid() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        AppointmentRequestDto invalidRequest = validRequest(
                start,
                start.minusMinutes(30)
        ).toBuilder()
                .doctorId(" ")
                .recipientEmail("invalid-email")
                .build();

        mockMvc.perform(post(API_CREATE_APPOINTMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("doctorId must not be blank")))
                .andExpect(jsonPath("$.message", containsString("recipientEmail must be a well-formed email address")))
                .andExpect(jsonPath("$.message", containsString("endDateTime must be after startDateTime")));
    }

    @Test
    void getAppointmentsByDoctorIdShouldReturnDefaultListWhenRangeIsMissing() throws Exception {
        when(appointmentService.getAppointmentsByDoctorId("doctor-2"))
                .thenReturn(List.of(appointmentResponse("appointment-2", AppointmentStatus.SCHEDULED)));

        mockMvc.perform(get(API_GET_APPOINTMENTS_BY_DOCTOR, "doctor-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("appointment-2"));
    }

    @Test
    void getAppointmentsByDoctorIdShouldReturnRangeResultsWhenBothDatesAreProvided() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 7, 3, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 3, 17, 0);

        when(appointmentService.getDoctorAppointmentsInRange("doctor-3", start, end))
                .thenReturn(List.of(appointmentResponse("appointment-3", AppointmentStatus.SCHEDULED)));

        mockMvc.perform(get(API_GET_APPOINTMENTS_BY_DOCTOR, "doctor-3")
                        .param("start", "2026-07-03T09:00:00")
                        .param("end", "2026-07-03T17:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("appointment-3"));
    }

    @Test
    void getAppointmentsByDoctorIdShouldReturn400WhenOnlyOneRangeValueIsProvided() throws Exception {
        mockMvc.perform(get(API_GET_APPOINTMENTS_BY_DOCTOR, "doctor-4")
                        .param("start", "2026-07-03T09:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Both start and end query parameters must be provided together."));
    }

    @Test
    void updateAppointmentStatusShouldReturn200WhenPayloadIsValid() throws Exception {
        AppointmentStatusUpdateRequestDto requestDto = AppointmentStatusUpdateRequestDto.builder()
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(appointmentService.updateAppointmentStatus(org.mockito.ArgumentMatchers.eq("appointment-5"), any(AppointmentStatusUpdateRequestDto.class)))
                .thenReturn(appointmentResponse("appointment-5", AppointmentStatus.COMPLETED));

        mockMvc.perform(patch(API_UPDATE_APPOINTMENT_STATUS, "appointment-5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("appointment-5"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(appointmentService).updateAppointmentStatus(org.mockito.ArgumentMatchers.eq("appointment-5"), any(AppointmentStatusUpdateRequestDto.class));
    }

    private AppointmentRequestDto validRequest(LocalDateTime start, LocalDateTime end) {
        return AppointmentRequestDto.builder()
                .doctorId("doctor-1")
                .patientId("patient-1")
                .startDateTime(start)
                .endDateTime(end)
                .reason("Consultation")
                .recipientEmail("patient@example.com")
                .build();
    }

    private AppointmentResponseDto appointmentResponse(String id, AppointmentStatus status) {
        return AppointmentResponseDto.builder()
                .id(id)
                .doctorId("doctor-1")
                .patientId("patient-1")
                .startDateTime(LocalDateTime.of(2026, 7, 3, 10, 0))
                .endDateTime(LocalDateTime.of(2026, 7, 3, 10, 30))
                .status(status)
                .reason("Consultation")
                .build();
    }
}
