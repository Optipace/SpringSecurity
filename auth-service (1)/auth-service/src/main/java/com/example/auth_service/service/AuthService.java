package com.example.auth_service.service;

import com.example.auth_service.client.AuditClient;
import com.example.auth_service.client.UserClient;
import com.example.auth_service.dto.*;
import com.example.auth_service.entity.*;
import com.example.auth_service.enums.UserStatusEnum;
import com.example.auth_service.repository.*;
import com.example.auth_service.util.JWTUtil;
import jakarta.ws.rs.BadRequestException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@AllArgsConstructor
public class AuthService {
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final EmailService emailService;
    private final UserClient userClient;
    private final AuditClient auditClient;
    private static final Logger logger= LoggerFactory.getLogger(AuthService.class);

    public UserResponse register(RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        try{
            UserResponse response=userClient.register(registerRequest);
            return response;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    public void login(LoginRequest loginRequest) {
        AuthUserResponse user=userClient.getUserByUsername(loginRequest.getUsername());
        String decodedPassword=new String(Base64.getDecoder().decode(loginRequest.getPassword()));
        if (!passwordEncoder.matches(decodedPassword, user.getPassword())) {
            logger.warn("Invalid password for user: {}", loginRequest.getUsername());
            throw new BadRequestException("Invalid Password");
        }
        UserStatusEnum status=user.getStatus();
        if(status==UserStatusEnum.BLOCKED){
            logger.warn("Blocked user attempted login: {}",loginRequest.getUsername());
            throw new BadRequestException("Your account has been blocked");
        }
            otpService.sendOtp(user.getEmail());
    }

    public RefreshResponse generateRefreshToken(RefreshRequest refreshRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshRequest.getRefreshToken()).orElseThrow(() -> new RuntimeException("Refresh token not found"));
        if(refreshToken.isRevoked()){
            throw new RuntimeException("Refresh token revoked");
        }
        if (refreshToken == null || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Invalid or expired refresh token");
        }
            UserResponse user=userClient.getUser(refreshToken.getUserId());
            AuthUserResponse userRole=userClient.getUserByUsername(user.getUsername());
            Long roleId=userRole.getRoleId();
            String accessToken = jwtUtil.generateToken(refreshToken.getUserId(),roleId);
            logger.info("Token refreshed successfully",accessToken);
            auditClient.save(new AuditRequest(user.getUsername(),"REFRESH","Token refresh"));
            return new RefreshResponse(accessToken, refreshToken.getToken());

        }

        public void logout(String refreshToken){
            RefreshToken token=refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new RuntimeException("Invalid refresh token"));
            refreshTokenRepository.delete(token);
            RefreshToken refreshTokenToSetRevoked=new RefreshToken();
            refreshTokenToSetRevoked.setRevoked(true);
            UserResponse user=userClient.getUser(token.getUserId());
            logger.info("User {} logged out successfully",user.getUsername());
            auditClient.save(new AuditRequest(user.getUsername(),"LOGOUT","User logged out"));
        }

        public LoginResponse verifyOtp(VerifyOtpRequest verifyOtpRequest){
            boolean valid=otpService.verifyOtp(verifyOtpRequest.getEmail(),verifyOtpRequest.getOtp());
            if(!valid){
                logger.warn("LOGIN FAILED");
                throw new RuntimeException("Invalid or expired OTP");
            }
            try{
                UserResponse user=userClient.getUserByEmail(verifyOtpRequest.getEmail());
                AuthUserResponse userRole=userClient.getUserByUsername(user.getUsername());
                Long roleId=userRole.getRoleId();
                String accessToken = jwtUtil.generateToken(user.getId(),roleId);
                LocalDateTime issuedTime=LocalDateTime.now();
                String refreshToken = jwtUtil.generateRefreshToken(user.getId(),issuedTime);
                RefreshToken refreshTokenObject = new RefreshToken();
                refreshTokenObject.setToken(refreshToken);
                refreshTokenObject.setUserId(user.getId());
                refreshTokenObject.setExpiryDate(LocalDateTime.now().plusDays(1));
                refreshTokenRepository.save(refreshTokenObject);
                logger.info("Refresh token generated");
                auditClient.save(new AuditRequest(user.getUsername(),"REFRESH TOKEN GENERATION","Refresh token generated"));
                logger.info("User {} logged in successfully",user.getUsername());
                auditClient.save(new AuditRequest(user.getUsername(),"LOGIN","User logged in"));
                return new LoginResponse(accessToken, refreshToken);
            }
            catch(Exception exception){
                exception.printStackTrace();
                throw exception;
            }
        }
    }
