package tn.iteam.meduserservice.controllers.specs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.meduserservice.dtos.requests.SigninRequestDto;
import tn.iteam.meduserservice.dtos.requests.SignupRequestDto;
import tn.iteam.meduserservice.dtos.responses.AuthResponseDto;
import tn.iteam.meduserservice.utils.ApiUtils;

@RequestMapping
public interface IAuthController {

    @PostMapping(ApiUtils.API_AUTH_SIGNUP)
    ResponseEntity<Void> signup(@RequestBody SignupRequestDto request);

    @PostMapping(ApiUtils.API_AUTH_SIGNIN)
    ResponseEntity<AuthResponseDto> signin(@RequestBody SigninRequestDto request);
}
