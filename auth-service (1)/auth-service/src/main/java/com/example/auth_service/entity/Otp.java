package com.example.auth_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String otp;
    private LocalDateTime expiry_time;
    private boolean verified;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getExpiry_time() {
        return expiry_time;
    }
    public void setExpiry_time(LocalDateTime expiry_time) {
        this.expiry_time = expiry_time;
    }

    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
