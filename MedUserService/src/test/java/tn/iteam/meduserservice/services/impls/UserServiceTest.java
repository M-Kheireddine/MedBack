package tn.iteam.meduserservice.services.impls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import tn.iteam.meduserservice.dtos.requests.DoctorRequestDto;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.ProfileImageContentDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
    void getAllUsersShouldReturnMappedUsers() {
        UserService userService = buildService();
        UserEntity doctor = doctorEntity(UUID.randomUUID(), "john@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        UserEntity patient = patientEntity(UUID.randomUUID(), "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);

        when(userRepository.findAll()).thenReturn(List.of(doctor, patient));

        List<UserResponseDto> responses = userService.getAllUsers();

        assertEquals(2, responses.size());
        assertEquals("john@medback.com", responses.get(0).getEmail());
        assertEquals(Role.DOCTOR, responses.get(0).getRole());
        assertEquals("patient@medback.com", responses.get(1).getEmail());
        assertEquals(Role.PATIENT, responses.get(1).getRole());
    }

    @Test
    void getUserByIdShouldReturnMappedUser() {
        UserService userService = buildService();
        UUID userId = UUID.randomUUID();
        UserEntity user = doctorEntity(userId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto response = userService.getUserById(userId.toString());

        assertEquals(userId, response.getId());
        assertEquals("doctor@medback.com", response.getEmail());
        assertEquals(Role.DOCTOR, response.getRole());
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
        DoctorEntity existingDoctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        existingDoctor.setPassword("current-password");
        existingDoctor.setClinicAddress("Old address");

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
    void updateDoctorShouldEncodeNewPasswordWhenProvided() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity existingDoctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        existingDoctor.setPassword("current-password");
        DoctorRequestDto requestDto = doctorRequest("doctor-updated@medback.com", "NewPassword123");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.existsByMedicalLicenseNumberAndIdNot(requestDto.getMedicalLicenseNumber(), doctorId)).thenReturn(false);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encoded-new-password");
        when(doctorRepository.save(any(DoctorEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DoctorResponseDto response = userService.updateDoctor(doctorId.toString(), requestDto);

        assertEquals("encoded-new-password", existingDoctor.getPassword());
        assertEquals("doctor-updated@medback.com", response.getEmail());
        verify(passwordEncoder).encode("NewPassword123");
    }

    @Test
    void updateDoctorShouldThrowWhenEmailAlreadyExists() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity existingDoctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        DoctorRequestDto requestDto = doctorRequest("duplicate@medback.com", "Password123");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(userRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(UserEntity.builder().id(UUID.randomUUID()).email(requestDto.getEmail()).build()));

        assertThrows(DuplicateResourceException.class, () -> userService.updateDoctor(doctorId.toString(), requestDto));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void updateDoctorShouldThrowWhenMedicalLicenseAlreadyExists() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity existingDoctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        DoctorRequestDto requestDto = doctorRequest("doctor-updated@medback.com", "Password123");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(doctorRepository.existsByMedicalLicenseNumberAndIdNot(requestDto.getMedicalLicenseNumber(), doctorId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.updateDoctor(doctorId.toString(), requestDto));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void getAllDoctorsShouldReturnMappedDoctors() {
        UserService userService = buildService();
        DoctorEntity firstDoctor = doctorEntity(UUID.randomUUID(), "doctor1@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        DoctorEntity secondDoctor = doctorEntity(UUID.randomUUID(), "doctor2@medback.com", "Jane", "Howard", "Dermatology", "LIC-200", Boolean.TRUE);

        when(doctorRepository.findAll()).thenReturn(List.of(firstDoctor, secondDoctor));

        List<DoctorResponseDto> responses = userService.getAllDoctors();

        assertEquals(2, responses.size());
        assertEquals("doctor1@medback.com", responses.get(0).getEmail());
        assertEquals("doctor2@medback.com", responses.get(1).getEmail());
    }

    @Test
    void getDoctorByIdShouldReturnMappedDoctor() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity doctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        DoctorResponseDto response = userService.getDoctorById(doctorId.toString());

        assertEquals(doctorId, response.getId());
        assertEquals("Cardiology", response.getSpecialty());
    }

    @Test
    void getDoctorByIdShouldThrowWhenDoctorDoesNotExist() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getDoctorById(doctorId.toString())
        );

        assertEquals("Doctor not found with id: " + doctorId, exception.getMessage());
    }

    @Test
    void deleteDoctorShouldDeleteResolvedDoctor() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity doctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        userService.deleteDoctor(doctorId.toString());

        verify(doctorRepository).delete(doctor);
    }

    @Test
    void searchPublicDoctorsShouldFilterBySearchAndSpecialty() {
        UserService userService = buildService();
        DoctorEntity matchingDoctor = doctorEntity(UUID.randomUUID(), "sam@medback.com", "Sam", "Hill", "Cardiology", "LIC-100", Boolean.TRUE);
        matchingDoctor.setClinicAddress("Main Street Clinic");
        DoctorEntity wrongSpecialtyDoctor = doctorEntity(UUID.randomUUID(), "sam-ortho@medback.com", "Sam", "Young", "Orthopedics", "LIC-101", Boolean.TRUE);
        wrongSpecialtyDoctor.setClinicAddress("Main Street Clinic");
        DoctorEntity inactiveDoctor = doctorEntity(UUID.randomUUID(), "sam-inactive@medback.com", "Sam", "Brown", "Cardiology", "LIC-102", Boolean.FALSE);
        inactiveDoctor.setClinicAddress("Main Street Clinic");

        when(doctorRepository.findAll()).thenReturn(List.of(matchingDoctor, wrongSpecialtyDoctor, inactiveDoctor));
        when(doctorDtoMapper.toDoctorDto(any(DoctorResponseDto.class))).thenAnswer(invocation -> {
            DoctorResponseDto response = invocation.getArgument(0);
            return DoctorDto.builder()
                    .id(response.getId())
                    .firstName(response.getFirstName())
                    .lastName(response.getLastName())
                    .email(response.getEmail())
                    .specialty(response.getSpecialty())
                    .clinicAddress(response.getClinicAddress())
                    .build();
        });

        List<DoctorDto> results = userService.searchPublicDoctors("sam", "Cardiology");

        assertEquals(1, results.size());
        assertEquals("Sam", results.get(0).getFirstName());
        assertEquals("Cardiology", results.get(0).getSpecialty());
    }

    @Test
    void searchPublicDoctorsShouldReturnAllActiveDoctorsWhenFiltersAreBlank() {
        UserService userService = buildService();
        DoctorEntity firstDoctor = doctorEntity(UUID.randomUUID(), "doctor1@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        DoctorEntity secondDoctor = doctorEntity(UUID.randomUUID(), "doctor2@medback.com", "Jane", "Howard", "Dermatology", "LIC-200", Boolean.TRUE);
        DoctorEntity inactiveDoctor = doctorEntity(UUID.randomUUID(), "inactive@medback.com", "Noah", "Brown", "Neurology", "LIC-300", Boolean.FALSE);

        when(doctorRepository.findAll()).thenReturn(List.of(firstDoctor, secondDoctor, inactiveDoctor));
        when(doctorDtoMapper.toDoctorDto(any(DoctorResponseDto.class))).thenAnswer(invocation -> {
            DoctorResponseDto response = invocation.getArgument(0);
            return DoctorDto.builder()
                    .id(response.getId())
                    .firstName(response.getFirstName())
                    .lastName(response.getLastName())
                    .specialty(response.getSpecialty())
                    .build();
        });

        List<DoctorDto> results = userService.searchPublicDoctors(" ", null);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doctor -> "John".equals(doctor.getFirstName())));
        assertTrue(results.stream().anyMatch(doctor -> "Jane".equals(doctor.getFirstName())));
    }

    @Test
    void getDoctorProfileShouldReturnMappedDoctorDto() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity doctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        DoctorDto expectedDto = DoctorDto.builder().id(doctorId).firstName("John").specialty("Cardiology").build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorDtoMapper.toDoctorDto(any(DoctorResponseDto.class))).thenReturn(expectedDto);

        DoctorDto response = userService.getDoctorProfile(doctorId.toString());

        assertSame(expectedDto, response);
        verify(doctorDtoMapper).toDoctorDto(any(DoctorResponseDto.class));
    }

    @Test
    void getDoctorSummaryShouldReturnMappedDoctorDto() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity doctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        DoctorDto expectedDto = DoctorDto.builder().id(doctorId).firstName("John").specialty("Cardiology").build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorDtoMapper.toDoctorDto(any(DoctorResponseDto.class))).thenReturn(expectedDto);

        DoctorDto response = userService.getDoctorSummary(doctorId.toString());

        assertSame(expectedDto, response);
    }

    @Test
    void getAllPatientsShouldReturnMappedPatients() {
        UserService userService = buildService();
        PatientEntity patient = patientEntity(UUID.randomUUID(), "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);

        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<PatientResponseDto> responses = userService.getAllPatients();

        assertEquals(1, responses.size());
        assertEquals("patient@medback.com", responses.get(0).getEmail());
        assertEquals("PAT-100-200-300", responses.get(0).getFunctionalId());
    }

    @Test
    void getPatientByIdShouldReturnMappedPatientWhenUuidIsUsed() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        PatientResponseDto response = userService.getPatientById(patientId.toString());

        assertEquals(patientId, response.getId());
        assertEquals("PAT-100-200-300", response.getFunctionalId());
    }

    @Test
    void getPatientByIdShouldResolveFunctionalIdWhenIdentifierIsNotUuid() {
        UserService userService = buildService();
        PatientEntity patient = patientEntity(UUID.randomUUID(), "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);

        when(patientRepository.findByFunctionalId("PAT-100-200-300")).thenReturn(Optional.of(patient));

        PatientResponseDto response = userService.getPatientById("PAT-100-200-300");

        assertEquals("PAT-100-200-300", response.getFunctionalId());
        assertEquals("Ariel", response.getFirstName());
    }

    @Test
    void archivePatientShouldSetPatientInactive() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(PatientEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponseDto response = userService.archivePatient(patientId.toString());

        assertFalse(patient.getIsActive());
        assertFalse(response.getIsActive());
    }

    @Test
    void getPatientProfileShouldReturnMappedPatientDto() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);
        PatientDto expectedDto = PatientDto.builder().id(patientId).firstName("Ariel").functionalId("PAT-100-200-300").build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientDtoMapper.toPatientDto(any(PatientResponseDto.class))).thenReturn(expectedDto);

        PatientDto response = userService.getPatientProfile(patientId.toString());

        assertSame(expectedDto, response);
    }

    @Test
    void getPatientSummaryShouldReturnMappedPatientDto() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);
        PatientDto expectedDto = PatientDto.builder().id(patientId).firstName("Ariel").functionalId("PAT-100-200-300").build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientDtoMapper.toPatientDto(any(PatientResponseDto.class))).thenReturn(expectedDto);

        PatientDto response = userService.getPatientSummary(patientId.toString());

        assertSame(expectedDto, response);
    }

    @Test
    void unarchivePatientShouldSetPatientActive() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.FALSE);
        PatientDto expectedDto = PatientDto.builder().id(patientId).firstName("Ariel").functionalId("PAT-100-200-300").build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(PatientEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(patientDtoMapper.toPatientDto(any(PatientResponseDto.class))).thenReturn(expectedDto);

        PatientDto response = userService.unarchivePatient(patientId.toString());

        assertTrue(patient.getIsActive());
        assertSame(expectedDto, response);
    }

    @Test
    void updateDoctorProfileImageShouldPersistEncodedImage() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity doctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        MockMultipartFile image = new MockMultipartFile("image", "avatar.png", "image/png", "doctor-image".getBytes(UTF_8));
        DoctorDto expectedDto = DoctorDto.builder().id(doctorId).firstName("John").build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(DoctorEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(doctorDtoMapper.toDoctorDto(any(DoctorResponseDto.class))).thenReturn(expectedDto);

        DoctorDto response = userService.updateDoctorProfileImage(doctorId.toString(), image);

        ArgumentCaptor<DoctorEntity> doctorCaptor = ArgumentCaptor.forClass(DoctorEntity.class);
        verify(doctorRepository).save(doctorCaptor.capture());
        assertEquals(Base64.getEncoder().encodeToString("doctor-image".getBytes(UTF_8)), doctorCaptor.getValue().getProfileImageBase64());
        assertEquals("image/png", doctorCaptor.getValue().getProfileImageContentType());
        assertSame(expectedDto, response);
    }

    @Test
    void updatePatientProfileImageShouldPersistEncodedImage() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);
        MockMultipartFile image = new MockMultipartFile("image", "avatar.png", "image/jpeg", "patient-image".getBytes(UTF_8));
        PatientDto expectedDto = PatientDto.builder().id(patientId).firstName("Ariel").functionalId("PAT-100-200-300").build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(PatientEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(patientDtoMapper.toPatientDto(any(PatientResponseDto.class))).thenReturn(expectedDto);

        PatientDto response = userService.updatePatientProfileImage(patientId.toString(), image);

        ArgumentCaptor<PatientEntity> patientCaptor = ArgumentCaptor.forClass(PatientEntity.class);
        verify(patientRepository).save(patientCaptor.capture());
        assertEquals(Base64.getEncoder().encodeToString("patient-image".getBytes(UTF_8)), patientCaptor.getValue().getProfileImageBase64());
        assertEquals("image/jpeg", patientCaptor.getValue().getProfileImageContentType());
        assertSame(expectedDto, response);
    }

    @Test
    void updateDoctorProfileImageShouldRejectEmptyImage() {
        UserService userService = buildService();
        UUID doctorId = UUID.randomUUID();
        DoctorEntity doctor = doctorEntity(doctorId, "doctor@medback.com", "John", "Smith", "Cardiology", "LIC-100", Boolean.TRUE);
        MockMultipartFile image = new MockMultipartFile("image", "avatar.png", "image/png", new byte[0]);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> userService.updateDoctorProfileImage(doctorId.toString(), image)
        );

        assertEquals("Profile image file is required.", exception.getMessage());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void updatePatientProfileImageShouldRejectNonImageFile() {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);
        MockMultipartFile image = new MockMultipartFile("image", "document.txt", "text/plain", "not-an-image".getBytes(UTF_8));

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> userService.updatePatientProfileImage(patientId.toString(), image)
        );

        assertEquals("Only image files are allowed for profile upload.", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void updatePatientProfileImageShouldWrapImageProcessingFailure() throws IOException {
        UserService userService = buildService();
        UUID patientId = UUID.randomUUID();
        PatientEntity patient = patientEntity(patientId, "patient@medback.com", "Ariel", "Richardson", "PAT-100-200-300", Boolean.TRUE);
        MultipartFile image = mock(MultipartFile.class);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/png");
        when(image.getBytes()).thenThrow(new IOException("boom"));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> userService.updatePatientProfileImage(patientId.toString(), image)
        );

        assertEquals("Unable to process the uploaded profile image.", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void getProfileImageShouldDecodeStoredImage() {
        UserService userService = buildService();
        UUID userId = UUID.randomUUID();
        byte[] imageBytes = "stored-image".getBytes(UTF_8);
        UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .password("encoded")
                .role(Role.DOCTOR)
                .isActive(Boolean.TRUE)
                .profileImageBase64(Base64.getEncoder().encodeToString(imageBytes))
                .profileImageContentType("image/png")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ProfileImageContentDto response = userService.getProfileImage(userId.toString());

        assertArrayEquals(imageBytes, response.getContent());
        assertEquals("image/png", response.getContentType());
    }

    @Test
    void getProfileImageShouldUseDefaultContentTypeWhenStoredValueIsMissing() {
        UserService userService = buildService();
        UUID userId = UUID.randomUUID();
        byte[] imageBytes = "stored-image".getBytes(UTF_8);
        UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .password("encoded")
                .role(Role.DOCTOR)
                .isActive(Boolean.TRUE)
                .profileImageBase64(Base64.getEncoder().encodeToString(imageBytes))
                .profileImageContentType(null)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ProfileImageContentDto response = userService.getProfileImage(userId.toString());

        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    void getProfileImageShouldThrowWhenProfileImageIsMissing() {
        UserService userService = buildService();
        UUID userId = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("John")
                .lastName("Smith")
                .email("doctor@medback.com")
                .password("encoded")
                .role(Role.DOCTOR)
                .isActive(Boolean.TRUE)
                .profileImageBase64(" ")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getProfileImage(userId.toString())
        );

        assertEquals("Profile image not found for user id: " + userId, exception.getMessage());
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

    @Test
    void getPatientByIdShouldThrowWhenFunctionalIdCannotBeResolved() {
        UserService userService = buildService();
        when(patientRepository.findByFunctionalId("PAT-999-999-999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getPatientById("PAT-999-999-999")
        );

        assertEquals("Patient not found with identifier: PAT-999-999-999", exception.getMessage());
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

    private DoctorEntity doctorEntity(UUID id,
                                      String email,
                                      String firstName,
                                      String lastName,
                                      String specialty,
                                      String medicalLicenseNumber,
                                      Boolean isActive) {
        return DoctorEntity.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password("encoded-password")
                .role(Role.DOCTOR)
                .isActive(isActive)
                .createdAt(LocalDateTime.of(2026, 7, 2, 11, 0))
                .specialty(specialty)
                .phoneNumber("123456789")
                .clinicAddress("Clinic street")
                .medicalLicenseNumber(medicalLicenseNumber)
                .build();
    }

    private PatientEntity patientEntity(UUID id,
                                        String email,
                                        String firstName,
                                        String lastName,
                                        String functionalId,
                                        Boolean isActive) {
        return PatientEntity.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password("encoded-password")
                .role(Role.PATIENT)
                .isActive(isActive)
                .createdAt(LocalDateTime.of(2026, 7, 2, 11, 0))
                .functionalId(functionalId)
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();
    }
}
