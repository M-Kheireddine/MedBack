package tn.iteam.mednotificationservice.services;

import tn.iteam.mednotificationservice.dtos.events.NotificationEvent;
import tn.iteam.mednotificationservice.dtos.responses.NotificationStatusResponseDto;

public interface NotificationService {
    void sendNotification(NotificationEvent event);

    NotificationStatusResponseDto getStatus();
}
