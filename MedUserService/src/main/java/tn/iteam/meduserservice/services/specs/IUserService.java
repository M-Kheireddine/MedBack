package tn.iteam.meduserservice.services.specs;

import tn.iteam.meduserservice.dtos.responses.UserResponseDto;

import java.util.List;

public interface IUserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(String userId);
}
