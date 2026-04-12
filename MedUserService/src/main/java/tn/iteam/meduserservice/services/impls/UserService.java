package tn.iteam.meduserservice.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.mappers.UserMapper;
import tn.iteam.meduserservice.repositories.UserRepository;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return this.userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(String userId) {
        return this.userRepository.findById(UUID.fromString(userId))
                .map(userMapper::toUserResponseDto)
                .orElseGet(null);
    }
}
