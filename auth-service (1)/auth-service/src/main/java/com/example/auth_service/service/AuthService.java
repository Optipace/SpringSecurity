package com.example.auth_service.service;

import com.example.auth_service.dto.LoginResponse;
import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.entity.User;
import com.example.auth_service.enums.UserStatus;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    public String register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return "User Registered Successfully";
    }

    public ResponseEntity<?> login(LoginRequest loginRequest){
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }
            String token = jwtUtil.generateToken(user.getUsername());
        System.out.println("token: "+token);
            return ResponseEntity.ok(new LoginResponse(token));
        }

    }

