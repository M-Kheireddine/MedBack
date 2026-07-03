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
import tn.iteam.meduserservice.dtos.requests.DoctorRequestDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.exceptions.GlobalExceptionHandler;
import tn.iteam.meduserservice.exceptions.ResourceNotFoundException;
import tn.iteam.meduserservice.filters.JwtAuthenticationFilter;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tn.iteam.meduserservice.utils.ApiUtils.API_CREATE_DOCTOR;
import static tn.iteam.meduserservice.utils.ApiUtils.API_GET_ALL_DOCTORS;
import static tn.iteam.meduserservice.utils.ApiUtils.API_GET_ALL_PATIENTS;
import static tn.iteam.meduserservice.utils.ApiUtils.API_GET_ALL_USERS;
import static tn.iteam.meduserservice.utils.ApiUtils.API_GET_DOCTOR_BY_ID;
import static tn.iteam.meduserservice.utils.ApiUtils.API_GET_PATIENT_BY_ID;
import static tn.iteam.meduserservice.utils.ApiUtils.API_GET_USER_BY_ID;
import static tn.iteam.meduserservice.utils.ApiUtils.API_UPDATE_DOCTOR;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getAllUsersShouldReturnMappedUsers() throws Exception {
        List<UserResponseDto> responseDtos = List.of(
                UserResponseDto.builder()
                        .id(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Smith")
                        .email("doctor@medback.com")
                        .role(Role.DOCTOR)
                        .createdAt(LocalDateTime.of(2026, 7, 3, 9, 0))
                        .isActive(Boolean.TRUE)
                        .build()
        );

        when(userService.getAllUsers()).thenReturn(responseDtos);

        mockMvc.perform(get(API_GET_ALL_USERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("doctor@medback.com"))
                .andExpect(jsonPath("$[0].role").value("DOCTOR"));
    }

    @Test
    void getUserByIdShouldReturnMappedUser() throws Exception {
        UserResponseDto responseDto = UserResponseDto.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .role(Role.DOCTOR)
                .createdAt(LocalDateTime.of(2026, 7, 3, 9, 0))
                .isActive(Boolean.TRUE)
                .build();

        when(userService.getUserById("user-1")).thenReturn(responseDto);

        mockMvc.perform(get(API_GET_USER_BY_ID, "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("doctor@medback.com"))
                .andExpect(jsonPath("$.role").value("DOCTOR"));
    }

    @Test
    void createDoctorShouldReturn201WhenRequestIsValid() throws Exception {
        DoctorRequestDto requestDto = DoctorRequestDto.builder()
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .password("Password123")
                .specialty("Cardiology")
                .phoneNumber("123456789")
                .clinicAddress("Clinic")
                .medicalLicenseNumber("LIC-1")
                .build();

        DoctorResponseDto responseDto = DoctorResponseDto.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .role(Role.DOCTOR)
                .createdAt(LocalDateTime.of(2026, 7, 2, 17, 0))
                .isActive(Boolean.TRUE)
                .specialty("Cardiology")
                .phoneNumber("123456789")
                .clinicAddress("Clinic")
                .medicalLicenseNumber("LIC-1")
                .build();

        when(userService.createDoctor(any(DoctorRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(API_CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("doctor@medback.com"))
                .andExpect(jsonPath("$.specialty").value("Cardiology"));
    }

    @Test
    void createDoctorShouldReturn400WhenRequestIsInvalid() throws Exception {
        DoctorRequestDto invalidRequest = DoctorRequestDto.builder()
                .firstName(" ")
                .lastName(" ")
                .email("invalid-email")
                .password("short")
                .specialty(" ")
                .medicalLicenseNumber(" ")
                .build();

        mockMvc.perform(post(API_CREATE_DOCTOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("firstName must not be blank")))
                .andExpect(jsonPath("$.message", containsString("password size must be between 8 and 2147483647")));
    }

    @Test
    void getDoctorByIdShouldReturn404WhenDoctorDoesNotExist() throws Exception {
        when(userService.getDoctorById("missing-doctor"))
                .thenThrow(new ResourceNotFoundException("Doctor not found with id: missing-doctor"));

        mockMvc.perform(get(API_GET_DOCTOR_BY_ID, "missing-doctor"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found with id: missing-doctor"));
    }

    @Test
    void getAllDoctorsShouldReturnMappedDoctors() throws Exception {
        List<DoctorResponseDto> responseDtos = List.of(
                DoctorResponseDto.builder()
                        .id(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Smith")
                        .email("doctor@medback.com")
                        .role(Role.DOCTOR)
                        .createdAt(LocalDateTime.of(2026, 7, 3, 9, 0))
                        .isActive(Boolean.TRUE)
                        .specialty("Cardiology")
                        .medicalLicenseNumber("LIC-1")
                        .build()
        );

        when(userService.getAllDoctors()).thenReturn(responseDtos);

        mockMvc.perform(get(API_GET_ALL_DOCTORS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialty").value("Cardiology"));
    }

    @Test
    void updateDoctorShouldReturnUpdatedDoctor() throws Exception {
        DoctorRequestDto requestDto = DoctorRequestDto.builder()
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .password("Password123")
                .specialty("Cardiology")
                .phoneNumber("123456789")
                .clinicAddress("Clinic")
                .medicalLicenseNumber("LIC-1")
                .build();

        DoctorResponseDto responseDto = DoctorResponseDto.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .role(Role.DOCTOR)
                .createdAt(LocalDateTime.of(2026, 7, 3, 9, 0))
                .isActive(Boolean.TRUE)
                .specialty("Cardiology")
                .medicalLicenseNumber("LIC-1")
                .build();

        when(userService.updateDoctor(org.mockito.ArgumentMatchers.eq("doctor-1"), any(DoctorRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put(API_UPDATE_DOCTOR, "doctor-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("doctor@medback.com"));
    }

    @Test
    void getAllPatientsShouldReturnMappedPatients() throws Exception {
        List<PatientResponseDto> responseDtos = List.of(
                PatientResponseDto.builder()
                        .id(UUID.randomUUID())
                        .firstName("Ariel")
                        .lastName("Richardson")
                        .email("patient@medback.com")
                        .role(Role.PATIENT)
                        .createdAt(LocalDateTime.of(2026, 7, 3, 9, 0))
                        .isActive(Boolean.TRUE)
                        .functionalId("PAT-100-200-300")
                        .build()
        );

        when(userService.getAllPatients()).thenReturn(responseDtos);

        mockMvc.perform(get(API_GET_ALL_PATIENTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].functionalId").value("PAT-100-200-300"));
    }

    @Test
    void getPatientByIdShouldReturnMappedPatient() throws Exception {
        PatientResponseDto responseDto = PatientResponseDto.builder()
                .id(UUID.randomUUID())
                .firstName("Ariel")
                .lastName("Richardson")
                .email("patient@medback.com")
                .role(Role.PATIENT)
                .createdAt(LocalDateTime.of(2026, 7, 3, 9, 0))
                .isActive(Boolean.TRUE)
                .functionalId("PAT-100-200-300")
                .build();

        when(userService.getPatientById("patient-1")).thenReturn(responseDto);

        mockMvc.perform(get(API_GET_PATIENT_BY_ID, "patient-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.functionalId").value("PAT-100-200-300"));
    }

    @Test
    void deleteDoctorShouldReturn204() throws Exception {
        mockMvc.perform(delete("/v1/admin/doctors/{doctorId}", "doctor-1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteDoctor("doctor-1");
    }
}
