package tn.iteam.mednotificationservice.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.iteam.mednotificationservice.dtos.responses.NotificationStatusResponseDto;
import tn.iteam.mednotificationservice.services.NotificationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void getNotificationStatusShouldReturnServiceStatus() {
        NotificationStatusResponseDto expectedStatus = NotificationStatusResponseDto.builder()
                .service("med-notification-service")
                .status("READY")
                .build();
        when(notificationService.getStatus()).thenReturn(expectedStatus);

        ResponseEntity<NotificationStatusResponseDto> response = notificationController.getNotificationStatus();

        assertEquals(200, response.getStatusCode().value());
        assertSame(expectedStatus, response.getBody());
        verify(notificationService).getStatus();
    }
}
