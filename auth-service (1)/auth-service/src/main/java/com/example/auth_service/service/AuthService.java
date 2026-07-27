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
//    private final UserRoleRepository userRoleRepository;
//    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
//    private final NotificationService notificationService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
//    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
//    private final AuditService auditService;
    private final EmailService emailService;
    private final UserClient userClient;
    private final AuditClient auditClient;
    private static final Logger logger= LoggerFactory.getLogger(AuthService.class);

    public UserResponse register(RegisterRequest registerRequest) {
        System.out.println("inside register method");
//        Role roleObject=new Role();
//        User user = new User();
//        user.setUsername(registerRequest.getUsername());
//        user.setEmail(registerRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
//        user.setStatus(UserStatusEnum.ACTIVE);
//        user.setRole(registerRequest.getRole());
//        userRepository.save(user);
//        System.out.println("user role: "+user.getRole());
//        Role role=roleRepository.findByRoleName(user.getRole()).orElseThrow(()->new RuntimeException("Role not found"));
//        UserRole userRole=new UserRole();
//        userRole.setUser(user);
//        userRole.setRole(role);
//        userRoleRepository.save(userRole);
//        System.out.println("sending web socket notification");
//        NotificationMessage notification = new NotificationMessage(user.getUsername(), "New user", "User " + user.getUsername() + " has been registered");
//        notificationService.sendBroadcast(notification);
//        System.out.println("notification sent");
//        return user;
//        UserResponse userResponse=new UserResponse();
//        userResponse.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        System.out.println("Password before encode: "+registerRequest.getPassword());
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        System.out.println("register request: "+registerRequest);
//        return userClient.register(registerRequest);
        try{
            UserResponse response=userClient.register(registerRequest);
            System.out.println("after feign");
            return response;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }

    }

    public void login(LoginRequest loginRequest) {
        System.out.println("inside login service");
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        User user=userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()->new BadRequestException("Invalid user"));
        AuthUserResponse user=userClient.getUserByUsername(loginRequest.getUsername());
//        System.out.println("user: "+user);
        System.out.println("username: "+user.getUsername());
        System.out.println("password: "+user.getPassword());
        String decodedPassword=new String(Base64.getDecoder().decode(loginRequest.getPassword()));
        System.out.println("desoced: "+decodedPassword);
        System.out.println("db hash: "+user.getPassword());
        if (!passwordEncoder.matches(decodedPassword, user.getPassword())) {
            System.out.println(passwordEncoder.matches("123456789","$2a$10$Rjes9yW8yrNHB6Hfb6.o9.31Z3p4/O8XI0zwQVTROaN/K2gI0tonu"));
//            boolean matches=passwordEncoder.matches(decodedPassword,user.getPassword());
//            System.out.println("matches: "+matches);
            System.out.println("error occurred");
            logger.warn("Invalid password for user: {}", loginRequest.getUsername());
            throw new BadRequestException("Invalid Password");
        }
        System.out.println("before status");
        UserStatusEnum status=user.getStatus();
        System.out.println("status: "+status);
        if(status==UserStatusEnum.BLOCKED){
            logger.warn("Blocked user attempted login: {}",loginRequest.getUsername());
            throw new BadRequestException("Your account has been blocked");
        }
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
            UserResponse user=userClient.getUser(refreshToken.getUserId());
            System.out.println("user: "+user);
            AuthUserResponse userRole=userClient.getUserByUsername(user.getUsername());
            Long roleId=userRole.getRoleId();
            String accessToken = jwtUtil.generateToken(refreshToken.getUserId(),roleId);
            System.out.println(accessToken);
            logger.info("Token refreshed successfully",accessToken);
//            auditService.save(refreshToken.getUser().getUsername(),"REFRESH","Token refresh");
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
            System.out.println("Valid: "+valid);
            if(!valid){
                logger.warn("LOGIN FAILED");
                throw new RuntimeException("Invalid or expired OTP");
            }
            try{
                UserResponse user=userClient.getUserByEmail(verifyOtpRequest.getEmail());
                System.out.println("user: "+user);
                AuthUserResponse userRole=userClient.getUserByUsername(user.getUsername());
                Long roleId=userRole.getRoleId();
                String accessToken = jwtUtil.generateToken(user.getId(),roleId);
                System.out.println("after access token");
                LocalDateTime issuedTime=LocalDateTime.now();
                System.out.println("issuedtime:"+issuedTime);
                String refreshToken = jwtUtil.generateRefreshToken(user.getId(),issuedTime);
                System.out.println("after generate refresh token function");
                RefreshToken refreshTokenObject = new RefreshToken();
                refreshTokenObject.setToken(refreshToken);
                refreshTokenObject.setUserId(user.getId());
                refreshTokenObject.setExpiryDate(LocalDateTime.now().plusDays(1));
                System.out.println(refreshTokenObject);
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
