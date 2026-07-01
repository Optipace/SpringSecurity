package com.example.auth_service.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegistrationResponse {
    private String type;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
