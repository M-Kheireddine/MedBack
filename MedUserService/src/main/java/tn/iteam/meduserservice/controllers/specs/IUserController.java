package tn.iteam.meduserservice.controllers.specs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.utils.ApiUtils;

import java.util.List;

@RequestMapping
public interface IUserController {

    @GetMapping(ApiUtils.USERS_GET_ALL_USERS)
    ResponseEntity<List<UserResponseDto>> getAllUsers();

    @GetMapping(ApiUtils.USERS_GET_USER_BY_ID)
    ResponseEntity<UserResponseDto> getUserById(@PathVariable("userId") String userId);
}
