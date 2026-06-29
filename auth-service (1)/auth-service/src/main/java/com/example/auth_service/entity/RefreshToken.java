package com.example.auth_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_id;
    private String token;
    private LocalDateTime expiry_time;
}
