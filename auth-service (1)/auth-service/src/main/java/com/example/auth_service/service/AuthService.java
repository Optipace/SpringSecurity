package com.example.auth_service.service;

import com.example.auth_service.dto.*;
import com.example.auth_service.entity.*;
import com.example.auth_service.enums.UserStatusEnum;
import com.example.auth_service.repository.*;
import com.example.auth_service.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final NotificationService notificationService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AuditService auditService;
    private final EmailService emailService;
    private static final Logger logger= LoggerFactory.getLogger(AuditService.class);

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

    public void login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        User user=userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()->new RuntimeException("User not found"));
        otpService.sendOtp(user.getEmail());
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
            logger.info("Token refreshed successfully",accessToken);
            auditService.save(refreshToken.getUser().getUsername(),"REFRESH","Token refresh");
            return new RefreshResponse(accessToken, refreshToken.getToken());
        }

        public void logout(String refreshToken){
            RefreshToken token=refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new RuntimeException("Invalid refresh token"));
            refreshTokenRepository.delete(token);
            RefreshToken refreshTokenToSetRevoked=new RefreshToken();
            refreshTokenToSetRevoked.setRevoked(true);
            logger.info("User {} logged out successfully",token.getUser().getUsername());
            auditService.save(token.getUser().getUsername(),"LOGOUT","User logged out");
        }

        public LoginResponse verifyOtp(VerifyOtpRequest verifyOtpRequest){
            boolean valid=otpService.verifyOtp(verifyOtpRequest.getEmail(),verifyOtpRequest.getOtp());
            System.out.println("Valid: "+valid);
            if(!valid){
                logger.warn("LOGIN FAILED");
                throw new RuntimeException("Invalid or expired OTP");
            }
            User user=userRepository.findByEmail(verifyOtpRequest.getEmail()).orElseThrow(()->new RuntimeException("User not found"));
            System.out.println("user: "+user);
            String accessToken = jwtUtil.generateToken(user.getUsername());
            String refreshToken = UUID.randomUUID().toString();
            RefreshToken refreshTokenObject = new RefreshToken();
            refreshTokenObject.setToken(refreshToken);
            refreshTokenObject.setUser(user);
            refreshTokenObject.setExpiryDate(LocalDateTime.now().plusDays(1));
            System.out.println(refreshTokenObject);
            refreshTokenRepository.save(refreshTokenObject);
            logger.info("Refresh token generated");
            auditService.save(user.getUsername(),"REFRESH TOKEN GENERATION","Refresh token generated");
            logger.info("User {} logged in successfully",user.getUsername());
            auditService.save(user.getUsername(),"LOGIN","User logged in");
            return new LoginResponse(accessToken, refreshToken);
        }
    }
