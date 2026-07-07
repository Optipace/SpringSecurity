package com.example.auth_service.controller;

import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UpdateRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.entity.User;
import com.example.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
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

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@RequestBody UpdateRequest updateRequest) {
        return ResponseEntity.ok(userService.updateUser(id,updateRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        System.out.println("Inside get profile method");
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }
}
