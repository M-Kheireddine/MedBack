package tn.iteam.meduserservice.services.impls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.iteam.meduserservice.dtos.requests.AuthRequestDto;
import tn.iteam.meduserservice.dtos.requests.AdminRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.requests.PatientRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.exceptions.DuplicateResourceException;
import tn.iteam.meduserservice.exceptions.ResourceNotFoundException;
import tn.iteam.meduserservice.mappers.UserMapper;
import tn.iteam.meduserservice.models.PatientEntity;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.models.UserEntity;
import tn.iteam.meduserservice.repositories.PatientRepository;
import tn.iteam.meduserservice.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
        SecurityContextHolder.clearContext();
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
    void registerPatientShouldThrowWhenEmailAlreadyExists() {
        PatientRegistrationRequestDto requestDto = PatientRegistrationRequestDto.builder()
                .firstName("Ariel")
                .lastName("Richardson")
                .email("ariel.richardson@medback.com")
                .password("Pa$$w0rd!")
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();

        when(userRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(UserEntity.builder().id(UUID.randomUUID()).email(requestDto.getEmail()).build()));

        assertThrows(DuplicateResourceException.class, () -> authService.registerPatient(requestDto));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void registerPatientShouldThrowWhenSocialSecurityNumberAlreadyExists() {
        PatientRegistrationRequestDto requestDto = PatientRegistrationRequestDto.builder()
                .firstName("Ariel")
                .lastName("Richardson")
                .email("ariel.richardson@medback.com")
                .password("Pa$$w0rd!")
                .birthDate(LocalDate.of(1992, 7, 15))
                .socialSecurityNumber("461")
                .bloodType("B-")
                .build();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.existsBySocialSecurityNumber(requestDto.getSocialSecurityNumber())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.registerPatient(requestDto));
        verify(patientRepository, never()).save(any());
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

    @Test
    void loginShouldThrowWhenAuthenticatedUserCannotBeFound() {
        AuthRequestDto requestDto = AuthRequestDto.builder()
                .email("missing@medback.com")
                .password("SecurePass123")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(requestDto)
        );

        assertEquals("User not found with email: missing@medback.com", exception.getMessage());
    }

    @Test
    void registerAdminShouldAllowBootstrapWhenNoAdminExists() {
        AdminRegistrationRequestDto requestDto = AdminRegistrationRequestDto.builder()
                .firstName("System")
                .lastName("Admin")
                .email("admin@medback.com")
                .password("AdminPass123")
                .build();
        UUID adminId = UUID.randomUUID();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(0L);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encoded-admin");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(adminId);
            return user;
        });

        UserResponseDto response = authService.registerAdmin(requestDto);

        assertEquals(adminId, response.getId());
        assertEquals(Role.ADMIN, response.getRole());
        assertEquals("admin@medback.com", response.getEmail());
    }

    @Test
    void registerAdminShouldThrowWhenAdminAlreadyExistsAndCurrentUserIsNotAdmin() {
        AdminRegistrationRequestDto requestDto = AdminRegistrationRequestDto.builder()
                .firstName("System")
                .lastName("Admin")
                .email("admin@medback.com")
                .password("AdminPass123")
                .build();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> authService.registerAdmin(requestDto)
        );

        assertEquals("Admin registration is only allowed for an authenticated admin after bootstrap.", exception.getMessage());
    }

    @Test
    void registerAdminShouldAllowWhenCurrentUserHasAdminRole() {
        AdminRegistrationRequestDto requestDto = AdminRegistrationRequestDto.builder()
                .firstName("Second")
                .lastName("Admin")
                .email("admin2@medback.com")
                .password("AdminPass123")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "admin@medback.com",
                        "ignored",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encoded-admin-2");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto response = authService.registerAdmin(requestDto);

        assertEquals(Role.ADMIN, response.getRole());
        assertEquals("admin2@medback.com", response.getEmail());
    }
}
