package com.example.auth_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String permission_name;

    public String getPermission_name() {
        return permission_name;
    }
    public void setPermission_name(String permission_name) {
        this.permission_name = permission_name;
    }
}
