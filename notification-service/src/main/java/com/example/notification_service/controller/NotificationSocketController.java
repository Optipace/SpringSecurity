package com.example.notification_service.controller;

import com.example.notification_service.dto.RegistrationResponse;
import com.example.notification_service.registry.NotificationSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class NotificationSocketController {
    private final NotificationSessionRegistry notificationSessionRegistry;
    @MessageMapping("/register")
    @SendToUser("/queue/registration")
    public RegistrationResponse register(Principal principal, @Header("simpSessionId")String sessionId){
        String username=principal.getName();
        notificationSessionRegistry.register(username,sessionId);
        return new RegistrationResponse("REGISTERED");
    }
}
