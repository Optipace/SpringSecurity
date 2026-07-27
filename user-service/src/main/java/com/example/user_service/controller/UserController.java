package com.example.user_service.controller;

import com.example.user_service.dto.AuthUserResponse;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UpdateRequest;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.entity.Permission;
import com.example.user_service.entity.User;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/admin/users")
    public User addUser(@Valid @RequestBody RegisterRequest registerRequest) {
        System.out.println("reached add user function");
        return userService.addUser(registerRequest);
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        System.out.println("starting get user function");
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
        System.out.println("inside revoke admin function");
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

    @PostMapping("/admin/permissions")
    public ResponseEntity<String> createPermission(@RequestBody Permission permission){
        userService.createPermission(permission);
        return ResponseEntity.ok("Permission created");
    }

    @PostMapping("/admin/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<String> assignPermission(@PathVariable Long roleId,@PathVariable Long permissionId){
        userService.assignPermission(roleId,permissionId);
        return ResponseEntity.ok("Permission assigned");
    }

    @DeleteMapping("/admin/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<String> deletePermission(@PathVariable Long roleId,@PathVariable Long permissionId) {
        System.out.println("reached delete permission method");
        userService.deletePermission(roleId,permissionId);
        return ResponseEntity.ok("Permission deleted successfully");
    }

    @GetMapping("/users/username/{username}")
    public ResponseEntity<AuthUserResponse> getUserByUsername(@PathVariable String username){
        System.out.println("reached username function");
        AuthUserResponse response=userService.getUserByUsername(username);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/users/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        System.out.println("inside register method");
        return userService.register(registerRequest);
    }

    @GetMapping("/internal/users/{id}")
    public ResponseEntity<UserResponse> getUserInternal(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUser(id));
    }
}
