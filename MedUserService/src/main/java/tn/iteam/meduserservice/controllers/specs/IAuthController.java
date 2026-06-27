package tn.iteam.meduserservice.controllers.specs;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.meduserservice.dtos.requests.AdminRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.requests.AuthRequestDto;
import tn.iteam.meduserservice.dtos.requests.PatientRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.utils.ApiUtils;

@RequestMapping
public interface IAuthController {
    @PostMapping(ApiUtils.API_REGISTER_ADMIN)
    ResponseEntity<UserResponseDto> registerAdmin(@Valid @RequestBody AdminRegistrationRequestDto requestDto);

    @PostMapping(ApiUtils.API_REGISTER_PATIENT)
    ResponseEntity<PatientResponseDto> registerPatient(@Valid @RequestBody PatientRegistrationRequestDto requestDto);

    @PostMapping(ApiUtils.API_LOGIN)
    ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto requestDto);
}
