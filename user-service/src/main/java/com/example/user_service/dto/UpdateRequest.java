package com.example.user_service.dto;

import com.example.user_service.entity.Role;
import com.example.user_service.enums.UserStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateRequest {
    private Long id;

    @NotBlank(message="Username is required")
    @Size(min=3,max=20,message="Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message="Email is required")
    @Email(message="Invalid email format")
    private String email;

    @NotBlank(message="Password is required")
    @Size(min=8,message="Password must be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatusEnum status;
    private Role role;

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

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}
