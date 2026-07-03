package tn.iteam.meduserservice.services.impls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.iteam.meduserservice.dtos.requests.DoctorRequestDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.exceptions.BusinessRuleException;
import tn.iteam.meduserservice.exceptions.DuplicateResourceException;
import tn.iteam.meduserservice.exceptions.ResourceNotFoundException;
import tn.iteam.meduserservice.mappers.DoctorDtoMapper;
import tn.iteam.meduserservice.mappers.PatientDtoMapper;
import tn.iteam.meduserservice.mappers.UserMapper;
import tn.iteam.meduserservice.models.DoctorEntity;
import tn.iteam.meduserservice.models.PatientEntity;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.models.UserEntity;
import tn.iteam.meduserservice.repositories.DoctorRepository;
import tn.iteam.meduserservice.repositories.PatientRepository;
import tn.iteam.meduserservice.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DoctorDtoMapper doctorDtoMapper;

    @Mock
    private PatientDtoMapper patientDtoMapper;

    private UserService buildService() {
        return new UserService(
                userRepository,
                doctorRepository,
                patientRepository,
                new UserMapper(),
                doctorDtoMapper,
                patientDtoMapper,
                passwordEncoder
        );
    }

    @Test
    void createDoctorShouldSaveEncodedDoctorAndReturnMappedResponse() {
        UserService userService = buildService();
        DoctorRequestDto requestDto = doctorRequest("doctor@medback.com", "Password123");
        UUID doctorId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 2, 11, 0);

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.existsByMedicalLicenseNumber(requestDto.getMedicalLicenseNumber())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encoded-password");
        when(doctorRepository.save(any(DoctorEntity.class))).thenAnswer(invocation -> {
            DoctorEntity doctor = invocation.getArgument(0);
            doctor.setId(doctorId);
            doctor.setCreatedAt(createdAt);
            return doctor;
        });

        DoctorResponseDto response = userService.createDoctor(requestDto);

        ArgumentCaptor<DoctorEntity> doctorCaptor = ArgumentCaptor.forClass(DoctorEntity.class);
        verify(doctorRepository).save(doctorCaptor.capture());
        DoctorEntity savedDoctor = doctorCaptor.getValue();

        assertEquals("encoded-password", savedDoctor.getPassword());
        assertEquals(Role.DOCTOR, savedDoctor.getRole());
        assertEquals(doctorId, response.getId());
        assertEquals("Cardiology", response.getSpecialty());
    }

    @Test
    void createDoctorShouldThrowWhenPasswordIsMissing() {
        UserService userService = buildService();
        DoctorRequestDto requestDto = doctorRequest("doctor@medback.com", "  ");

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> userService.createDoctor(requestDto)
        );

        assertEquals("Doctor password is required when creating a doctor account.", exception.getMessage());
    }

    @Test
    void createDoctorShouldThrowWhenEmailAlreadyExists() {
        UserService userService = buildService();
        DoctorRequestDto requestDto = doctorRequest("doctor@medback.com", "Password123");

        when(userRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(UserEntity.builder().id(UUID.randomUUID()).email(requestDto.getEmail()).build()));

        assertThrows(DuplicateResourceException.class, () -> userService.createDoctor(requestDto));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void createDoctorShouldThrowWhenMedicalLicenseAlreadyExists() {
        UserService userService = buildService();
        DoctorRequestDto requestDto = doctorRequest("doctor@medback.com", "Password123");

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.existsByMedicalLicenseNumber(requestDto.getMedicalLicenseNumber())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createDoctor(requestDto));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void updateDoctorShouldKeepCurrentPasswordWhenNewPasswordIsBlank() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity existingDoctor = DoctorEntity.builder()
                .id(doctorId)
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .password("current-password")
                .role(Role.DOCTOR)
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.of(2026, 7, 2, 11, 0))
                .specialty("Cardiology")
                .phoneNumber("123456789")
                .clinicAddress("Old address")
                .medicalLicenseNumber("LIC-100")
                .build();

        DoctorRequestDto requestDto = doctorRequest("doctor-updated@medback.com", " ");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.existsByMedicalLicenseNumberAndIdNot(requestDto.getMedicalLicenseNumber(), doctorId)).thenReturn(false);
        when(doctorRepository.save(any(DoctorEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DoctorResponseDto response = userService.updateDoctor(doctorId.toString(), requestDto);

        assertEquals("current-password", existingDoctor.getPassword());
        verify(passwordEncoder, never()).encode(any(CharSequence.class));
        assertEquals("doctor-updated@medback.com", response.getEmail());
    }

    @Test
    void archivePatientShouldSetPatientInactive() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = PatientEntity.builder()
                .id(patientId)
                .firstName("Ariel")
                .lastName("Richardson")
                .email("patient@medback.com")
                .password("encoded")
                .role(Role.PATIENT)
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.of(2026, 7, 2, 11, 0))
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(PatientEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponseDto response = userService.archivePatient(patientId.toString());

        assertFalse(patient.getIsActive());
        assertFalse(response.getIsActive());
    }

    @Test
    void getUserByIdShouldThrowWhenUserDoesNotExist() {
        UserService userService = buildService();
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(userId.toString())
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    private DoctorRequestDto doctorRequest(String email, String password) {
        return DoctorRequestDto.builder()
                .firstName("John")
                .lastName("Smith")
                .email(email)
                .password(password)
                .specialty("Cardiology")
                .phoneNumber("123456789")
                .clinicAddress("Clinic street")
                .medicalLicenseNumber("LIC-100")
                .build();
    }
}
