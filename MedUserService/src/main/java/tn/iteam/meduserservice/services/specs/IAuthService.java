package tn.iteam.meduserservice.services.specs;

import tn.iteam.meduserservice.dtos.requests.SigninRequestDto;
import tn.iteam.meduserservice.dtos.requests.SignupRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;

public interface IAuthService {
    void signup(SignupRequestDto request);
    AuthResponseDto signin(SigninRequestDto request);
}
