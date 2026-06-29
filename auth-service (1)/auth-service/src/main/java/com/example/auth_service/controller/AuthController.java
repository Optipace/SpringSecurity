package com.example.auth_service.controller;

import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
}
