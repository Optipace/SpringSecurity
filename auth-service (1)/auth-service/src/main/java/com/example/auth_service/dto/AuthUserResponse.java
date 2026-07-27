package com.example.auth_service.dto;

//import com.example.auth_service.entity.Role;

import com.example.auth_service.enums.UserStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class AuthUserResponse {
    private String username;
    private String password;
    private String email;
    private Long roleId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public UserStatusEnum getStatus() {
        return status;
    }
    public void setStatus(UserStatusEnum status) {
        this.status = status;
    }
}
