package tn.iteam.meduserservice.controllers.impls;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.iteam.meduserservice.dtos.requests.AuthRequestDto;
import tn.iteam.meduserservice.dtos.requests.PatientRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.exceptions.GlobalExceptionHandler;
import tn.iteam.meduserservice.services.specs.IAuthService;
import tn.iteam.meduserservice.models.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tn.iteam.meduserservice.utils.ApiUtils.API_LOGIN;
import static tn.iteam.meduserservice.utils.ApiUtils.API_REGISTER_PATIENT;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAuthService authService;

    @Test
    void registerPatientShouldReturn201WhenRequestIsValid() throws Exception {
        PatientRegistrationRequestDto requestDto = PatientRegistrationRequestDto.builder()
                .firstName("Ariel")
                .lastName("Richardson")
                .email("ariel@medback.com")
                .password("Pa$$w0rd!")
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();

        PatientResponseDto responseDto = PatientResponseDto.builder()
                .id(UUID.randomUUID())
                .firstName("Ariel")
                .lastName("Richardson")
                .email("ariel@medback.com")
                .role(Role.PATIENT)
                .createdAt(LocalDateTime.of(2026, 7, 2, 16, 0))
                .isActive(Boolean.TRUE)
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();

        when(authService.registerPatient(any(PatientRegistrationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_REGISTER_PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ariel"))
                .andExpect(jsonPath("$.role").value("PATIENT"));
    }

    @Test
    void registerPatientShouldReturn400WhenRequestIsInvalid() throws Exception {
        PatientRegistrationRequestDto invalidRequest = PatientRegistrationRequestDto.builder()
                .firstName(" ")
                .lastName(" ")
                .email("invalid-email")
                .password("short")
                .socialSecurityNumber(" ")
                .build();

        mockMvc.perform(post(API_REGISTER_PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("firstName must not be blank")))
                .andExpect(jsonPath("$.message", containsString("lastName must not be blank")))
                .andExpect(jsonPath("$.message", containsString("email must be a well-formed email address")));
    }

    @Test
    void loginShouldReturn200WhenCredentialsAreValid() throws Exception {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .email("doctor@medback.com")
                .password("Password123")
                .build();

        AuthResponseDto responseDto = AuthResponseDto.builder()
                .accessToken("jwt-token")
                .tokenType("Bearer")
                .expiresAt(LocalDateTime.of(2026, 7, 3, 12, 0))
                .user(UserResponseDto.builder()
                        .id(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Doe")
                        .email("doctor@medback.com")
                        .role(Role.DOCTOR)
                        .createdAt(LocalDateTime.of(2026, 7, 2, 16, 30))
                        .isActive(Boolean.TRUE)
                        .build())
                .build();

        when(authService.login(any(AuthRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.user.role").value("DOCTOR"));
    }
}
