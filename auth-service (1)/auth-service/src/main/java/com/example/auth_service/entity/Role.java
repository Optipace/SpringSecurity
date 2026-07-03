package com.example.auth_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="role_name")
    private String roleName;
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String role_name) {
        this.roleName = role_name;
    }
}
