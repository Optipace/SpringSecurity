package com.example.auth_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_id;
    private Long role_id;
}
