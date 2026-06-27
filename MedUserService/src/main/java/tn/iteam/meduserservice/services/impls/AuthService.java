package tn.iteam.meduserservice.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.iteam.meduserservice.dtos.requests.AdminRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.requests.AuthRequestDto;
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
import tn.iteam.meduserservice.services.specs.IAuthService;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public UserResponseDto registerAdmin(AdminRegistrationRequestDto requestDto) {
        validateEmailAvailability(requestDto.getEmail(), null);
        long adminCount = userRepository.countByRole(Role.ADMIN);
        if (adminCount > 0 && !currentUserIsAdmin()) {
            throw new AccessDeniedException("Admin registration is only allowed for an authenticated admin after bootstrap.");
        }

        UserEntity admin = UserEntity.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.ADMIN)
                .isActive(Boolean.TRUE)
                .build();

        return userMapper.toUserResponseDto(userRepository.save(admin));
    }

    @Override
    public PatientResponseDto registerPatient(PatientRegistrationRequestDto requestDto) {
        validateEmailAvailability(requestDto.getEmail(), null);
        if (patientRepository.existsBySocialSecurityNumber(requestDto.getSocialSecurityNumber())) {
            throw new DuplicateResourceException("A patient with this social security number already exists.");
        }

        PatientEntity patient = PatientEntity.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.PATIENT)
                .isActive(Boolean.TRUE)
                .birthDate(requestDto.getBirthDate())
                .socialSecurityNumber(requestDto.getSocialSecurityNumber())
                .bloodType(requestDto.getBloodType())
                .build();

        return userMapper.toPatientResponseDto(patientRepository.save(patient));
    }

    @Override
    public AuthResponseDto login(AuthRequestDto requestDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                requestDto.getEmail(),
                requestDto.getPassword()
        ));

        UserEntity user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requestDto.getEmail()));

        String accessToken = jwtService.generateToken(user);
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresAt(jwtService.extractExpiration(accessToken))
                .user(userMapper.toUserResponseDto(user))
                .build();
    }

    private void validateEmailAvailability(String email, java.util.UUID userId) {
        userRepository.findByEmail(email)
                .filter(user -> userId == null || !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new DuplicateResourceException("A user with this email already exists.");
                });
    }

    private boolean currentUserIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
