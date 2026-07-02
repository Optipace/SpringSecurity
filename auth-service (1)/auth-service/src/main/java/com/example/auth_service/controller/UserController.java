package com.example.auth_service.controller;

import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.entity.User;
import com.example.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> userResponseDetails(Authentication authentication) {
        return ResponseEntity.ok(userService.userResponseDetails(authentication.getName()));
    }
}
