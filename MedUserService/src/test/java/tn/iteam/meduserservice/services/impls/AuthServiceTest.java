package tn.iteam.meduserservice.services.impls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.iteam.meduserservice.dtos.requests.AuthRequestDto;
import tn.iteam.meduserservice.dtos.requests.PatientRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.mappers.UserMapper;
import tn.iteam.meduserservice.models.PatientEntity;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.models.UserEntity;
import tn.iteam.meduserservice.repositories.PatientRepository;
import tn.iteam.meduserservice.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                patientRepository,
                new UserMapper(),
                passwordEncoder,
                authenticationManager,
                jwtService
        );
    }

    @Test
    void registerPatientShouldSaveEncodedPatientAndReturnMappedResponse() {
        PatientRegistrationRequestDto requestDto = PatientRegistrationRequestDto.builder()
                .firstName("Ariel")
                .lastName("Richardson")
                .email("ariel.richardson@medback.com")
                .password("Pa$$w0rd!")
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();
        UUID patientId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 30, 10, 0);

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.existsBySocialSecurityNumber(requestDto.getSocialSecurityNumber())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encoded-password");
        when(patientRepository.save(any(PatientEntity.class))).thenAnswer(invocation -> {
            PatientEntity patient = invocation.getArgument(0);
            patient.setId(patientId);
            patient.setCreatedAt(createdAt);
            return patient;
        });

        PatientResponseDto response = authService.registerPatient(requestDto);

        ArgumentCaptor<PatientEntity> patientCaptor = ArgumentCaptor.forClass(PatientEntity.class);
        verify(patientRepository).save(patientCaptor.capture());
        PatientEntity savedPatient = patientCaptor.getValue();

        assertEquals("Ariel", savedPatient.getFirstName());
        assertEquals("Richardson", savedPatient.getLastName());
        assertEquals("ariel.richardson@medback.com", savedPatient.getEmail());
        assertEquals("encoded-password", savedPatient.getPassword());
        assertEquals(Role.PATIENT, savedPatient.getRole());
        assertTrue(savedPatient.getIsActive());
        assertEquals(LocalDate.of(1992, 7, 15), savedPatient.getBirthDate());
        assertEquals("461", savedPatient.getSocialSecurityNumber());
        assertEquals("B-", savedPatient.getBloodType());

        assertEquals(patientId, response.getId());
        assertEquals("Ariel", response.getFirstName());
        assertEquals("ariel.richardson@medback.com", response.getEmail());
        assertEquals(Role.PATIENT, response.getRole());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals("461", response.getSocialSecurityNumber());
    }

    @Test
    void loginShouldAuthenticateAndReturnJwtPayload() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .email("doctor@medback.com")
                .password("SecurePass123")
                .build();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 30, 12, 15);
        LocalDateTime expiresAt = LocalDateTime.of(2026, 7, 1, 12, 15);
        UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("Nina")
                .lastName("Howard")
                .email("doctor@medback.com")
                .password("encoded-secret")
                .role(Role.DOCTOR)
                .isActive(Boolean.TRUE)
                .createdAt(createdAt)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.extractExpiration("jwt-token")).thenReturn(expiresAt);

        AuthResponseDto response = authService.login(requestDto);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(requestDto.getEmail());

        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(expiresAt, response.getExpiresAt());
        assertEquals(userId, response.getUser().getId());
        assertEquals("Nina", response.getUser().getFirstName());
        assertEquals(Role.DOCTOR, response.getUser().getRole());
        assertTrue(response.getUser().getIsActive());
    }
}
