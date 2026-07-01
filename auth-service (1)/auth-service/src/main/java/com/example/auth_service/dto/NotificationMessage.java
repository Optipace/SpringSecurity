package com.example.auth_service.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotificationMessage {
    private Long userId;
    private String title;
    private String message;
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
