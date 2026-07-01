package com.example.auth_service.controller;

import com.example.auth_service.dto.NotificationMessage;
import com.example.auth_service.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping("/send")
    public ResponseEntity<String> sendNotifications(@RequestBody NotificationMessage notificationMessage) {
        notificationService.sendNotifications(notificationMessage);
        return ResponseEntity.ok("Notification sent");
    }
}
