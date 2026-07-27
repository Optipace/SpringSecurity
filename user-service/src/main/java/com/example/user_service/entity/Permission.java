package com.example.user_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="permission_name")
    private String permissionName;

    public String getPermissionName() {
        return permissionName;
    }
    public void setPermissionName(String permission_name) {
        this.permissionName = permission_name;
    }
}
