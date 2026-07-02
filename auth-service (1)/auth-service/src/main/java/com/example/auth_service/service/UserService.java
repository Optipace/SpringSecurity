package com.example.auth_service.service;

import com.example.auth_service.dto.NotificationMessage;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.User;
import com.example.auth_service.entity.UserRole;
import com.example.auth_service.enums.UserStatusEnum;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final NotificationService notificationService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public User register(RegisterRequest registerRequest) {
        Role roleObject = new Role();
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        userRepository.save(user);
        System.out.println("User got saved" + user.getId());
        Role role = roleRepository.findByRoleName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        System.out.println("sending web socket notification");
        NotificationMessage notification = new NotificationMessage(user.getId(), "New user", "User" + user.getUsername() + "has been registered");
        notificationService.sendNotifications(notification);
        System.out.println("notification sent");
        return user;
    }

    public UserResponse userResponseDetails(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}