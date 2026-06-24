package tn.iteam.mednotificationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.mednotificationservice.dtos.responses.NotificationStatusResponseDto;
import tn.iteam.mednotificationservice.services.NotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/status")
    public ResponseEntity<NotificationStatusResponseDto> getNotificationStatus() {
        return ResponseEntity.ok(notificationService.getStatus());
    }
}
