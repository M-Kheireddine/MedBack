package tn.iteam.meduserservice.services.specs;

import tn.iteam.meduserservice.dtos.requests.DoctorRequestDto;
import tn.iteam.meduserservice.dtos.responses.DoctorDto;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientDto;
import tn.iteam.meduserservice.dtos.responses.ProfileImageContentDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(String userId);

    DoctorResponseDto createDoctor(DoctorRequestDto requestDto);

    List<DoctorResponseDto> getAllDoctors();

    DoctorResponseDto getDoctorById(String doctorId);

    DoctorResponseDto updateDoctor(String doctorId, DoctorRequestDto requestDto);

    void deleteDoctor(String doctorId);

    List<DoctorDto> searchPublicDoctors(String search, String specialty);

    DoctorDto getDoctorProfile(String doctorId);

    DoctorDto getDoctorSummary(String doctorId);

    List<PatientResponseDto> getAllPatients();

    PatientResponseDto getPatientById(String patientId);

    PatientResponseDto archivePatient(String patientId);

    PatientDto getPatientProfile(String patientId);

    PatientDto getPatientSummary(String patientId);

    PatientDto unarchivePatient(String patientId);

    DoctorDto updateDoctorProfileImage(String doctorId, MultipartFile image);

    PatientDto updatePatientProfileImage(String patientId, MultipartFile image);

    ProfileImageContentDto getProfileImage(String userId);
}
