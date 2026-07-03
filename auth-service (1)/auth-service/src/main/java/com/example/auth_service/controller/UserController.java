package com.example.auth_service.controller;

import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.entity.User;
import com.example.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping()
    public User addUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.addUser(registerRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
