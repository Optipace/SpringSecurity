package com.example.auth_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="role_permissions")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long role_id;
    private Long permission_id;
}
