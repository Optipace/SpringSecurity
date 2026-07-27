package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationMessage;
import com.example.notification_service.registry.NotificationSessionRegistry;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationSessionRegistry notificationSessionRegistry;
    public String sendPrivateNotification(NotificationMessage notificationMessage){
        if(!notificationSessionRegistry.isOnline(notificationMessage.getUsername())){
            return "User is not connected";
        }
        String destination="/queue/notifications/"+notificationMessage.getUsername();
        simpMessagingTemplate.convertAndSend(destination,notificationMessage);
        return "Notification sent";
    }

    public String sendBroadcast(NotificationMessage notificationMessage){
        simpMessagingTemplate.convertAndSend("/topic/company-announcements",notificationMessage);
        return "Broadcast sent";
    }
}
