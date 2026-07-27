package com.example.auth_service.dto;

import com.example.auth_service.enums.UserStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status;
    private String role;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatusEnum getStatus() {
        return status;
    }
    public void setStatus(UserStatusEnum status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, String password, UserStatusEnum status, String role, boolean blocked) {
        this.id=id;
        this.username=username;
        this.email=email;
        this.password=password;
        this.status=status;
        this.role=role;
    }
}
