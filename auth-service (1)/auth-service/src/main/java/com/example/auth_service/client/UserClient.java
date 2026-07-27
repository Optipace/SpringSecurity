package com.example.auth_service.client;

import com.example.auth_service.dto.AuthUserResponse;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UserResponse;
//import com.example.auth_service.entity.User;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="user-service")
public interface UserClient {
    @PostMapping("/admin/users")
    UserResponse addUser(@Valid @RequestBody RegisterRequest registerRequest);
    @GetMapping("/users/username/{username}")
    AuthUserResponse getUserByUsername(@PathVariable String username);
    @GetMapping("/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable String email);
    @GetMapping("/internal/users/{id}")
    UserResponse getUser(@PathVariable Long id);
    @PostMapping("/users/register")
    UserResponse register(@Valid @RequestBody RegisterRequest registerRequest);
}
