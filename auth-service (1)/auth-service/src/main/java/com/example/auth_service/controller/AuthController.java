package com.example.auth_service.controller;

import com.example.auth_service.dto.*;
import com.example.auth_service.entity.User;
import com.example.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> userResponseDetails(Authentication authentication) {
        return ResponseEntity.ok(authService.userResponseDetails(authentication.getName()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> generateRefreshToken(@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.generateRefreshToken(refreshRequest));
    }
}
