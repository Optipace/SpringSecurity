//package com.example.auth_service.listener;
//
//import com.example.auth_service.registry.NotificationSessionRegistry;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Component
//@RequiredArgsConstructor
//public class WebSocketEvents {
//    private final NotificationSessionRegistry notificationSessionRegistry;
//    @EventListener
//    public void handleDisconnect(SessionDisconnectEvent sessionDisconnectEvent){
//        notificationSessionRegistry.unregisterBySessionId(sessionDisconnectEvent.getSessionId());
//    }
//}
