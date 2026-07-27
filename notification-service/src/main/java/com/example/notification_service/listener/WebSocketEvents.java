package com.example.notification_service.listener;

import com.example.notification_service.registry.NotificationSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEvents {
    private final NotificationSessionRegistry notificationSessionRegistry;
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent sessionDisconnectEvent){
        notificationSessionRegistry.unregisterBySessionId(sessionDisconnectEvent.getSessionId());
    }
}
