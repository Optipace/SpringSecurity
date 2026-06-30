package com.example.auth_service.service;

import com.example.auth_service.dto.*;
import com.example.auth_service.entity.User;
import com.example.auth_service.enums.UserStatus;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final SimpMessagingTemplate simpMessagingTemplate;
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
        System.out.println("sending web socket notification");
        NotificationMessage notification=new NotificationMessage("User registered","New user "+user.getUsername()+"registered");
        simpMessagingTemplate.convertAndSend("/topic/users",notification);
        System.out.println("notification sent");
        return user;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            String token = jwtUtil.generateToken(loginRequest.getUsername());
        System.out.println("token: "+token);
            return ResponseEntity.ok(new LoginResponse(token));
        }

//    public UserResponse userResponseDetails(String authHeader) {
//
//        if(authHeader==null || !authHeader.startsWith("Bearer ")){
//            throw new RuntimeException("Authorization header missing");
//        }
//        String token=authHeader.substring(7);
//        if(!jwtUtil.validateToken(token)){
//            throw new RuntimeException("Invalid token");
//        }
//        String username= jwtUtil.extractUsername(token);
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
//        return new UserResponse(user.getId(),user.getUsername(),user.getEmail());
//    }


    }

