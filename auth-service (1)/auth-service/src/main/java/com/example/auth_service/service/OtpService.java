package com.example.auth_service.service;

import com.example.auth_service.entity.*;
import com.example.auth_service.repository.*;
import com.example.auth_service.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class OtpService {
    private final AuditService auditService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    public void sendOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        Optional<Otp> existingOtp = otpRepository.findByEmail(email);
        Otp otpEntity = new Otp();
        if (existingOtp.isPresent()) {
            otpEntity = existingOtp.get();
            otpEntity.setOtp(otp);
            otpEntity.setExpiry_time(LocalDateTime.now().plusMinutes(5));
        } else {
            otpEntity.setEmail(email);
            otpEntity.setOtp(otp);
            otpEntity.setExpiry_time(LocalDateTime.now().plusMinutes(5));
        }
        otpRepository.save(otpEntity);
        emailService.sendEmail("anjala050@gmail.com", "OTP Verification", "Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes");
    }

    public boolean verifyOtp(String email,String otp){
        Optional<Otp> optionalOtp=otpRepository.findByEmail(email);
        if(optionalOtp.isEmpty()){
            return false;
        }
        Otp otpEntity=optionalOtp.get();
        if(!otpEntity.getOtp().equals(otp)){
            return false;
        }
        if(otpEntity.getExpiry_time().isBefore(LocalDateTime.now())){
            return false;
        }
        otpRepository.delete(otpEntity);
        return true;
    }
}
