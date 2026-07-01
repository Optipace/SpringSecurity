package com.example.auth_service.service;

import com.example.auth_service.dto.*;
import com.example.auth_service.entity.User;
import com.example.auth_service.enums.UserStatus;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final NotificationService notificationService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public User register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        System.out.println("User got saved"+user.getId());
        System.out.println("sending web socket notification");
        NotificationMessage notification = new NotificationMessage(user.getId(),"New user","User"+user.getUsername()+"has been registered");
        notificationService.sendNotifications(notification);
        System.out.println("notification sent");
        return user;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        System.out.println("token: " + token);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    public UserResponse userResponseDetails(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(user.getId(),user.getUsername(),user.getEmail());
    }
}

