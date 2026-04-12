package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.meduserservice.controllers.specs.IUserController;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.services.specs.IUserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController implements IUserController {
    private final IUserService userService;

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(String userId) {
        return ResponseEntity.ok(this.userService.getUserById(userId));
    }
}
