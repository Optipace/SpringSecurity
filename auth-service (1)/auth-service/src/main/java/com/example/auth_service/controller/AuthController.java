package com.example.auth_service.controller;

import com.example.auth_service.dto.*;
import com.example.auth_service.entity.User;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.OtpService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        authService.login(loginRequest);
        return ResponseEntity.ok("OTP sent to your mail");
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> generateRefreshToken(@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.generateRefreshToken(refreshRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest){
        otpService.sendOtp(otpRequest.getEmail());
        return ResponseEntity.ok("OTP Sent Successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        boolean verified= otpService.verifyOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtp());
        if(verified){
            return ResponseEntity.ok("OTP Verified Successfully");
        }
        return ResponseEntity.badRequest().body("Invalid OTP");
    }
}
