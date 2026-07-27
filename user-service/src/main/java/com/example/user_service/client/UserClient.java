package com.example.user_service.client;

import com.example.user_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="user-service")
public interface UserClient {
    @GetMapping("/admin/users/{id}")
    UserResponse getUser(@PathVariable Long id);
}
