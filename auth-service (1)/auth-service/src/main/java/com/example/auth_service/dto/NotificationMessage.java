package com.example.auth_service.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotificationMessage {
    private String username;
    private String title;
    private String message;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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
