package tn.iteam.medcoreservice.controllers.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import tn.iteam.medcoreservice.dtos.requests.MedicationRequestDto;
import tn.iteam.medcoreservice.dtos.responses.MedicationResponseDto;
import tn.iteam.medcoreservice.exceptions.GlobalExceptionHandler;
import tn.iteam.medcoreservice.exceptions.ResourceNotFoundException;
import tn.iteam.medcoreservice.services.impls.IMedicationService;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_ADMIN_CREATE_MEDICATION;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_ADMIN_GET_ALL_MEDICATIONS;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_ADMIN_GET_MEDICATION_BY_ID;
import static tn.iteam.medcoreservice.utils.ApiUtils.API_CREATE_MEDICATION;

@WebMvcTest(MedicationController.class)
@Import(GlobalExceptionHandler.class)
class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IMedicationService medicationService;

    @Test
    void getAdminMedicationsShouldReturn200AndResponseBody() throws Exception {
        List<MedicationResponseDto> medications = List.of(
                medicationResponse("med-1", "Amoxicillin", "/images/amoxicillin.png"),
                medicationResponse("med-2", "Ibuprofen", "/images/ibuprofen.png")
        );

        when(medicationService.getAllMedications()).thenReturn(medications);

        mockMvc.perform(get(API_ADMIN_GET_ALL_MEDICATIONS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("med-1"))
                .andExpect(jsonPath("$[0].name").value("Amoxicillin"))
                .andExpect(jsonPath("$[1].id").value("med-2"))
                .andExpect(jsonPath("$[1].name").value("Ibuprofen"));
    }

    @Test
    void getAdminMedicationByIdShouldReturn200WhenMedicationExists() throws Exception {
        MedicationResponseDto responseDto = medicationResponse("med-42", "Paracetamol", "/images/paracetamol.png");

        when(medicationService.getMedicationById("med-42")).thenReturn(responseDto);

        mockMvc.perform(get(API_ADMIN_GET_MEDICATION_BY_ID, "med-42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("med-42"))
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.category").value("Analgesic"))
                .andExpect(jsonPath("$.imageUrl").value("/images/paracetamol.png"));
    }

    @Test
    void getAdminMedicationByIdShouldReturn404WhenMedicationDoesNotExist() throws Exception {
        when(medicationService.getMedicationById("missing-id"))
                .thenThrow(new ResourceNotFoundException("Medication not found with id: missing-id"));

        mockMvc.perform(get(API_ADMIN_GET_MEDICATION_BY_ID, "missing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Medication not found with id: missing-id"))
                .andExpect(jsonPath("$.path").value("/v1/admin/medications/missing-id"));
    }

    @Test
    void createMedicationShouldReturn201AndMappedBody() throws Exception {
        MedicationRequestDto requestDto = medicationRequest("Aspirin", "Analgesic");
        MedicationResponseDto responseDto = medicationResponse("med-50", "Aspirin", "/images/aspirin.png");

        when(medicationService.createMedication(any(MedicationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_CREATE_MEDICATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("med-50"))
                .andExpect(jsonPath("$.name").value("Aspirin"))
                .andExpect(jsonPath("$.category").value("Analgesic"));

        ArgumentCaptor<MedicationRequestDto> requestCaptor = ArgumentCaptor.forClass(MedicationRequestDto.class);
        verify(medicationService).createMedication(requestCaptor.capture());
        assertEquals("Aspirin", requestCaptor.getValue().getName());
        assertEquals("Analgesic", requestCaptor.getValue().getCategory());
    }

    @Test
    void createMedicationShouldReturn400WhenRequestBodyIsInvalid() throws Exception {
        MedicationRequestDto invalidRequest = medicationRequest("   ", "   ");

        mockMvc.perform(post(API_CREATE_MEDICATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", allOf(
                        containsString("name must not be blank"),
                        containsString("category must not be blank")
                )))
                .andExpect(jsonPath("$.path").value("/v1/medications"));
    }

    @Test
    void createAdminMedicationShouldReturn201WhenMultipartPayloadIsValid() throws Exception {
        String medicationJson = """
                {
                  "name": "Vitamin C",
                  "description": "<p>Immune support</p>",
                  "category": "Supplement",
                  "laboratory": "Bayer",
                  "imageUrl": "https://cdn.example.com/vitamin-c.png"
                }
                """;

        MockMultipartFile medicationPart = new MockMultipartFile(
                "medication",
                "medication.json",
                MediaType.APPLICATION_JSON_VALUE,
                medicationJson.getBytes()
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "vitamin-c.png",
                MediaType.IMAGE_PNG_VALUE,
                "image-content".getBytes()
        );

        MedicationResponseDto responseDto = MedicationResponseDto.builder()
                .id("med-60")
                .name("Vitamin C")
                .description("<p>Immune support</p>")
                .category("Supplement")
                .laboratory("Bayer")
                .imageUrl("/images/vitamin-c.png")
                .build();
        when(medicationService.createAdminMedication(any(MedicationRequestDto.class), any())).thenReturn(responseDto);

        mockMvc.perform(multipart(API_ADMIN_CREATE_MEDICATION)
                        .file(medicationPart)
                        .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("med-60"))
                .andExpect(jsonPath("$.name").value("Vitamin C"))
                .andExpect(jsonPath("$.category").value("Supplement"));

        ArgumentCaptor<MedicationRequestDto> requestCaptor = ArgumentCaptor.forClass(MedicationRequestDto.class);
        verify(medicationService).createAdminMedication(requestCaptor.capture(), any());
        assertEquals("Vitamin C", requestCaptor.getValue().getName());
        assertEquals("Supplement", requestCaptor.getValue().getCategory());
        assertEquals("https://cdn.example.com/vitamin-c.png", requestCaptor.getValue().getImageUrl());
    }

    @Test
    void createAdminMedicationShouldReturn400WhenMedicationJsonIsInvalid() throws Exception {
        MockMultipartFile medicationPart = new MockMultipartFile(
                "medication",
                "medication.json",
                MediaType.APPLICATION_JSON_VALUE,
                "{invalid-json}".getBytes()
        );

        mockMvc.perform(multipart(API_ADMIN_CREATE_MEDICATION)
                        .file(medicationPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("medication part must contain valid JSON."))
                .andExpect(jsonPath("$.path").value("/v1/admin/medications"));
    }

    private MedicationRequestDto medicationRequest(String name, String category) {
        return MedicationRequestDto.builder()
                .name(name)
                .description("<p>Pain relief</p>")
                .category(category)
                .laboratory("Bayer")
                .imageUrl("/images/aspirin.png")
                .build();
    }

    private MedicationResponseDto medicationResponse(String id, String name, String imageUrl) {
        return MedicationResponseDto.builder()
                .id(id)
                .name(name)
                .description("<p>Description</p>")
                .category("Analgesic")
                .laboratory("Bayer")
                .imageUrl(imageUrl)
                .build();
    }
}
