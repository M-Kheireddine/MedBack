package tn.iteam.meduserservice.controllers.impls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tn.iteam.meduserservice.dtos.responses.ProfileImageContentDto;
import tn.iteam.meduserservice.services.specs.IUserService;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private FileController fileController;

    @Test
    void getProfileImageShouldReturnInlineImageResponse() {
        byte[] content = "image-content".getBytes();
        ProfileImageContentDto imageContent = ProfileImageContentDto.builder()
                .content(content)
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .build();
        when(userService.getProfileImage("user-1")).thenReturn(imageContent);

        ResponseEntity<byte[]> response = fileController.getProfileImage("user-1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        assertEquals("inline; filename=\"profile-image\"", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertArrayEquals(content, response.getBody());
        verify(userService).getProfileImage("user-1");
    }
}
