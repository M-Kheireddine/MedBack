package tn.iteam.meduserservice.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.iteam.meduserservice.dtos.requests.DoctorRequestDto;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
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
import tn.iteam.meduserservice.services.specs.IUserService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final DoctorDtoMapper doctorDtoMapper;
    private final PatientDtoMapper patientDtoMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(String userId) {
        return userMapper.toUserResponseDto(findUserById(userId));
    }

    @Override
    public DoctorResponseDto createDoctor(DoctorRequestDto requestDto) {
        if (requestDto.getPassword() == null || requestDto.getPassword().isBlank()) {
            throw new BusinessRuleException("Doctor password is required when creating a doctor account.");
        }
        validateEmailAvailability(requestDto.getEmail(), null);
        if (doctorRepository.existsByMedicalLicenseNumber(requestDto.getMedicalLicenseNumber())) {
            throw new DuplicateResourceException("A doctor with this medical license number already exists.");
        }

        DoctorEntity doctor = DoctorEntity.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.DOCTOR)
                .isActive(Boolean.TRUE)
                .specialty(requestDto.getSpecialty())
                .phoneNumber(requestDto.getPhoneNumber())
                .clinicAddress(requestDto.getClinicAddress())
                .medicalLicenseNumber(requestDto.getMedicalLicenseNumber())
                .build();

        return userMapper.toDoctorResponseDto(doctorRepository.save(doctor));
    }

    @Override
    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(userMapper::toDoctorResponseDto)
                .toList();
    }

    @Override
    public DoctorResponseDto getDoctorById(String doctorId) {
        return userMapper.toDoctorResponseDto(findDoctorById(doctorId));
    }

    @Override
    public DoctorResponseDto updateDoctor(String doctorId, DoctorRequestDto requestDto) {
        DoctorEntity doctor = findDoctorById(doctorId);
        validateEmailAvailability(requestDto.getEmail(), doctor.getId());
        if (doctorRepository.existsByMedicalLicenseNumberAndIdNot(requestDto.getMedicalLicenseNumber(), doctor.getId())) {
            throw new DuplicateResourceException("A doctor with this medical license number already exists.");
        }

        doctor.setFirstName(requestDto.getFirstName());
        doctor.setLastName(requestDto.getLastName());
        doctor.setEmail(requestDto.getEmail());
        doctor.setSpecialty(requestDto.getSpecialty());
        doctor.setPhoneNumber(requestDto.getPhoneNumber());
        doctor.setClinicAddress(requestDto.getClinicAddress());
        doctor.setMedicalLicenseNumber(requestDto.getMedicalLicenseNumber());
        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            doctor.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        return userMapper.toDoctorResponseDto(doctorRepository.save(doctor));
    }

    @Override
    public void deleteDoctor(String doctorId) {
        doctorRepository.delete(findDoctorById(doctorId));
    }

    @Override
    public List<DoctorDto> searchPublicDoctors(String search, String specialty) {
        return doctorRepository.findAll()
                .stream()
                .filter(doctor -> Boolean.TRUE.equals(doctor.getIsActive()))
                .filter(doctor -> matchesSearch(doctor, search))
                .filter(doctor -> matchesSpecialty(doctor, specialty))
                .map(userMapper::toDoctorResponseDto)
                .map(doctorDtoMapper::toDoctorDto)
                .toList();
    }

    @Override
    public DoctorDto getDoctorProfile(String doctorId) {
        return doctorDtoMapper.toDoctorDto(userMapper.toDoctorResponseDto(findDoctorById(doctorId)));
    }

    @Override
    public DoctorDto getDoctorSummary(String doctorId) {
        return doctorDtoMapper.toDoctorDto(userMapper.toDoctorResponseDto(findDoctorById(doctorId)));
    }

    @Override
    public List<PatientResponseDto> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(userMapper::toPatientResponseDto)
                .toList();
    }

    @Override
    public PatientResponseDto getPatientById(String patientId) {
        return userMapper.toPatientResponseDto(findPatientById(patientId));
    }

    @Override
    public PatientResponseDto archivePatient(String patientId) {
        PatientEntity patient = findPatientById(patientId);
        patient.setIsActive(Boolean.FALSE);
        return userMapper.toPatientResponseDto(patientRepository.save(patient));
    }

    @Override
    public PatientDto getPatientProfile(String patientId) {
        return patientDtoMapper.toPatientDto(userMapper.toPatientResponseDto(findPatientById(patientId)));
    }

    @Override
    public PatientDto getPatientSummary(String patientId) {
        return patientDtoMapper.toPatientDto(userMapper.toPatientResponseDto(findPatientById(patientId)));
    }

    @Override
    public PatientDto unarchivePatient(String patientId) {
        PatientEntity patient = findPatientById(patientId);
        patient.setIsActive(Boolean.TRUE);
        return patientDtoMapper.toPatientDto(userMapper.toPatientResponseDto(patientRepository.save(patient)));
    }

    private UserEntity findUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private DoctorEntity findDoctorById(String doctorId) {
        return doctorRepository.findById(UUID.fromString(doctorId))
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
    }

    private PatientEntity findPatientById(String patientId) {
        return patientRepository.findById(UUID.fromString(patientId))
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
    }

    private void validateEmailAvailability(String email, UUID userId) {
        userRepository.findByEmail(email)
                .filter(user -> userId == null || !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new DuplicateResourceException("A user with this email already exists.");
                });
    }

    private boolean matchesSearch(DoctorEntity doctor, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String normalizedSearch = search.trim().toLowerCase(Locale.ROOT);
        return containsValue(doctor.getFirstName(), normalizedSearch)
                || containsValue(doctor.getLastName(), normalizedSearch)
                || containsValue(doctor.getSpecialty(), normalizedSearch)
                || containsValue(doctor.getClinicAddress(), normalizedSearch);
    }

    private boolean matchesSpecialty(DoctorEntity doctor, String specialty) {
        if (specialty == null || specialty.isBlank()) {
            return true;
        }

        return doctor.getSpecialty() != null
                && doctor.getSpecialty().trim().equalsIgnoreCase(specialty.trim());
    }

    private boolean containsValue(String value, String normalizedSearch) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedSearch);
    }
}
