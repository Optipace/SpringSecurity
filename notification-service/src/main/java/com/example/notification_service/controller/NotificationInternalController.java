package com.example.notification_service.controller;

import com.example.notification_service.dto.NotificationMessage;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications/internal")
@RequiredArgsConstructor
@Slf4j
public class NotificationInternalController {
    private final NotificationService notificationService;
    @PostMapping("/send")
    public ResponseEntity<String> sendPrivateNotification(@RequestBody NotificationMessage notificationMessage){
        log.info("Sending private notification to {}",notificationMessage.getUsername());
        return ResponseEntity.ok(notificationService.sendPrivateNotification(notificationMessage));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> sendBroadcast(@RequestBody NotificationMessage notificationMessage){
        log.info("Sending broadcast notification");
        return ResponseEntity.ok(notificationService.sendBroadcast(notificationMessage));
    }
}
