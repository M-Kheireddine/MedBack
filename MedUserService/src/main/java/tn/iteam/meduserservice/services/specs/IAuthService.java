package tn.iteam.meduserservice.services.specs;

import tn.iteam.meduserservice.dtos.requests.AdminRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.requests.AuthRequestDto;
import tn.iteam.meduserservice.dtos.requests.PatientRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;

public interface IAuthService {
    UserResponseDto registerAdmin(AdminRegistrationRequestDto requestDto);

    PatientResponseDto registerPatient(PatientRegistrationRequestDto requestDto);

    AuthResponseDto login(AuthRequestDto requestDto);
}
