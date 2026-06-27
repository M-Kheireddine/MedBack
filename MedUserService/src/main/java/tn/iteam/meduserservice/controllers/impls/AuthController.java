package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.meduserservice.controllers.specs.IAuthController;
import tn.iteam.meduserservice.dtos.requests.AdminRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.requests.AuthRequestDto;
import tn.iteam.meduserservice.dtos.requests.PatientRegistrationRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.services.specs.IAuthService;

@RestController
@RequiredArgsConstructor
public class AuthController implements IAuthController {
    private final IAuthService authService;

    @Override
    public ResponseEntity<UserResponseDto> registerAdmin(AdminRegistrationRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAdmin(requestDto));
    }

    @Override
    public ResponseEntity<PatientResponseDto> registerPatient(PatientRegistrationRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerPatient(requestDto));
    }

    @Override
    public ResponseEntity<AuthResponseDto> login(AuthRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }
}
