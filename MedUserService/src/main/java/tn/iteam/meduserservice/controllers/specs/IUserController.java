package tn.iteam.meduserservice.controllers.specs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.utils.ApiUtils;

import java.util.List;

@RequestMapping(ApiUtils.USERS)
public interface IUserController {

    @GetMapping
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @GetMapping()
    ResponseEntity<UserResponseDto> getUserById(String userId);
}
