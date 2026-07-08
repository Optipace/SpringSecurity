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
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    public User addUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return userService.addUser(registerRequest);
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/admin/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateRequest updateRequest) {
        return ResponseEntity.ok(userService.updateUser(id, updateRequest));
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/users/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        System.out.println("Inside get profile method");
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    @PutMapping("/admin/{id}/revoke-admin")
    public ResponseEntity<String> revokeAdminRole(@PathVariable Long id) {
        return ResponseEntity.ok(userService.revokeAdminRole(id));
    }

    @PatchMapping("/admin/users/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    @PatchMapping("/admin/users/{id}/unblock")
    public ResponseEntity<String> unblockUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.unblockUser(id));
    }
}
