package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.meduserservice.controllers.specs.IAuthController;
import tn.iteam.meduserservice.dtos.requests.SigninRequestDto;
import tn.iteam.meduserservice.dtos.requests.SignupRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.services.specs.IAuthService;

@RestController
@RequiredArgsConstructor
public class AuthController implements IAuthController {

    private final IAuthService authService;

    @Override
    public ResponseEntity<Void> signup(SignupRequestDto request) {
        authService.signup(request);
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<AuthResponseDto> signin(SigninRequestDto request) {
        return ResponseEntity.ok(authService.signin(request));
    }
}
