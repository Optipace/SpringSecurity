package com.example.auth_service.controller;

import com.example.auth_service.dto.*;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.OtpService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        System.out.println("Reached register method");
        authService.register(registerRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("inside login method");
        authService.login(loginRequest);
        return ResponseEntity.ok("OTP sent to your mail");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> generateRefreshToken(@RequestBody RefreshRequest refreshRequest) {
        try{
            return ResponseEntity.ok(authService.generateRefreshToken(refreshRequest));
        }catch (RuntimeException runtimeException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(runtimeException.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

//    @PostMapping("/send-otp")
//    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest){
//        otpService.sendOtp(otpRequest.getEmail());
//        return ResponseEntity.ok("OTP Sent Successfully");
//    }

    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponse> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest) {
        System.out.println("reached verify otp");
        return ResponseEntity.ok(authService.verifyOtp(verifyOtpRequest));
    }
}
