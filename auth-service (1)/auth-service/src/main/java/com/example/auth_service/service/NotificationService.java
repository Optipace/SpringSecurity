package com.example.auth_service.service;

import com.example.auth_service.dto.NotificationMessage;
import com.example.auth_service.websocket.NotificationWebSocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationWebSocketHandler notificationWebSocketHandler;
    public void sendNotifications(NotificationMessage notificationMessage){
        notificationWebSocketHandler.sendNotifications(notificationMessage);
    }
}
