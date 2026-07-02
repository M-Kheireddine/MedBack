package tn.iteam.meduserservice.controllers.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.meduserservice.controllers.specs.IFileController;
import tn.iteam.meduserservice.dtos.responses.ProfileImageContentDto;
import tn.iteam.meduserservice.services.specs.IUserService;

@RestController
@RequiredArgsConstructor
public class FileController implements IFileController {
    private final IUserService userService;

    @Override
    public ResponseEntity<byte[]> getProfileImage(String userId) {
        ProfileImageContentDto imageContent = userService.getProfileImage(userId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"profile-image\"")
                .contentType(MediaType.parseMediaType(imageContent.getContentType()))
                .body(imageContent.getContent());
    }
}
