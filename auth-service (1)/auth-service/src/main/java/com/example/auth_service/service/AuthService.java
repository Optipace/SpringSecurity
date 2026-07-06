package com.example.auth_service.service;

import com.example.auth_service.dto.*;
import com.example.auth_service.entity.*;
import com.example.auth_service.enums.UserStatusEnum;
import com.example.auth_service.repository.RefreshTokenRepository;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.repository.UserRoleRepository;
import com.example.auth_service.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final NotificationService notificationService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AuditService auditService;

    public User register(RegisterRequest registerRequest) {
        Role roleObject=new Role();
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setRole(registerRequest.getRole());
        userRepository.save(user);
        System.out.println("user role: "+user.getRole());
        Role role=roleRepository.findByRoleName(user.getRole()).orElseThrow(()->new RuntimeException("Role not found"));
        UserRole userRole=new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        System.out.println("sending web socket notification");
        NotificationMessage notification = new NotificationMessage(user.getId(), "New user", "User" + user.getUsername() + "has been registered");
        notificationService.sendNotifications(notification);
        System.out.println("notification sent");
        return user;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String accessToken = jwtUtil.generateToken(loginRequest.getUsername());
        String refreshToken = UUID.randomUUID().toString();
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        UserRole userRole=userRoleRepository.findByUserUsername(user.getUsername()).orElseThrow(()->new UsernameNotFoundException("User role not found"));
        Role role=roleRepository.findByRoleName("USER").orElseThrow(()->new RuntimeException("Role not found"));
        RefreshToken refreshTokenObject = new RefreshToken();
        refreshTokenObject.setToken(refreshToken);
        refreshTokenObject.setUser(user);
        refreshTokenObject.setExpiryDate(LocalDateTime.now().plusDays(7));
        System.out.println(refreshTokenObject);
        refreshTokenRepository.save(refreshTokenObject);
        auditService.save(loginRequest.getUsername(),"LOGIN","User logged in");
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));

    }

    public RefreshResponse generateRefreshToken(RefreshRequest refreshRequest) {
        System.out.println("starting this function     :");
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshRequest.getRefreshToken()).orElseThrow(() -> new RuntimeException("Refresh token not found"));
        if(refreshToken.isRevoked()){
            throw new RuntimeException("Refresh token revoked");
        }
        System.out.println("REfresh token     :"+refreshToken);
        if (refreshToken == null || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Invalid or expired refresh token");
        }
            String accessToken = jwtUtil.generateToken(refreshToken.getUser().getUsername());
            System.out.println(accessToken);
            return new RefreshResponse(accessToken, refreshToken.getToken());
        }

        public void logout(String refreshToken){
            RefreshToken token=refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new RuntimeException("Invalid refresh token"));
            refreshTokenRepository.delete(token);
            RefreshToken refreshTokenToSetRevoked=new RefreshToken();
            refreshTokenToSetRevoked.setRevoked(true);
            auditService.save(token.getUser().getUsername(),"LOGOUT","User logged out");
        }
    }
