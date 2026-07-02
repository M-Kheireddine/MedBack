package tn.iteam.medcoreservice.controllers.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionLineRequestDto;
import tn.iteam.medcoreservice.dtos.requests.PrescriptionRequestDto;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.exceptions.GlobalExceptionHandler;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.models.PrescriptionLine;
import tn.iteam.medcoreservice.services.impls.IPrescriptionService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_CREATE_PRESCRIPTION;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_GET_PRESCRIPTION_BY_ID;

@WebMvcTest(PrescriptionController.class)
@Import(GlobalExceptionHandler.class)
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPrescriptionService prescriptionService;

    @Test
    void createPrescriptionShouldReturn201WhenPayloadIsValid() throws Exception {
        PrescriptionRequestDto requestDto = validRequest();
        PrescriptionResponseDto responseDto = prescriptionResponse("prescription-1");

        when(prescriptionService.createPrescription(any(PrescriptionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_CREATE_PRESCRIPTION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("prescription-1"))
                .andExpect(jsonPath("$.doctorId").value("doctor-1"))
                .andExpect(jsonPath("$.patientId").value("patient-1"));

        verify(prescriptionService).createPrescription(any(PrescriptionRequestDto.class));
    }

    @Test
    void createPrescriptionShouldReturn400WhenPayloadIsInvalid() throws Exception {
        PrescriptionRequestDto invalidRequest = PrescriptionRequestDto.builder()
                .doctorId(" ")
                .patientId(" ")
                .prescriptionLines(List.of())
                .recipientEmail("invalid-email")
                .build();

        mockMvc.perform(post(API_CREATE_PRESCRIPTION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("doctorId must not be blank")))
                .andExpect(jsonPath("$.message", containsString("patientId must not be blank")))
                .andExpect(jsonPath("$.message", containsString("prescriptionLines must not be empty")))
                .andExpect(jsonPath("$.message", containsString("recipientEmail must be a well-formed email address")));
    }

    @Test
    void getPrescriptionByIdShouldReturn200WhenPrescriptionExists() throws Exception {
        when(prescriptionService.getPrescriptionById("prescription-42"))
                .thenReturn(prescriptionResponse("prescription-42"));

        mockMvc.perform(get(API_GET_PRESCRIPTION_BY_ID, "prescription-42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prescription-42"))
                .andExpect(jsonPath("$.doctorId").value("doctor-1"));
    }

    @Test
    void getPrescriptionByIdShouldReturn404WhenPrescriptionDoesNotExist() throws Exception {
        when(prescriptionService.getPrescriptionById("missing-prescription"))
                .thenThrow(new ResourceNotFoundException("Prescription not found with id: missing-prescription"));

        mockMvc.perform(get(API_GET_PRESCRIPTION_BY_ID, "missing-prescription"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Prescription not found with id: missing-prescription"));
    }

    @Test
    void deletePrescriptionShouldReturn204() throws Exception {
        mockMvc.perform(delete("/v1/prescriptions/{prescriptionId}", "prescription-88"))
                .andExpect(status().isNoContent());

        verify(prescriptionService).deletePrescription("prescription-88");
    }

    private PrescriptionRequestDto validRequest() {
        return PrescriptionRequestDto.builder()
                .doctorId("doctor-1")
                .patientId("patient-1")
                .doctorNotes("Doctor notes")
                .recipientEmail("patient@example.com")
                .prescriptionLines(List.of(
                        PrescriptionLineRequestDto.builder()
                                .medicationId("med-1")
                                .dosage("1 tablet")
                                .duration("5 days")
                                .build()
                ))
                .build();
    }

    private PrescriptionResponseDto prescriptionResponse(String id) {
        return PrescriptionResponseDto.builder()
                .id(id)
                .doctorId("doctor-1")
                .patientId("patient-1")
                .createdAt(LocalDateTime.of(2026, 7, 2, 12, 0))
                .doctorNotes("Doctor notes")
                .prescriptionLines(List.of(
                        PrescriptionLine.builder()
                                .medicationId("med-1")
                                .dosage("1 tablet")
                                .duration("5 days")
                                .build()
                ))
                .build();
    }
}
