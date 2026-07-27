package com.example.auth_service.service;

import com.example.auth_service.entity.*;
import com.example.auth_service.repository.*;
import com.example.auth_service.util.JWTUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class OtpService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    @Transactional
    public void sendOtp(String email) {
        try {
            String otp = String.valueOf(100000 + new Random().nextInt(900000));
            otpRepository.deleteByEmail(email);
            Otp otpEntity = new Otp();
            otpEntity.setEmail(email);
            otpEntity.setOtp(otp);
            otpEntity.setExpiry_time(LocalDateTime.now().plusMinutes(15));
            otpRepository.save(otpEntity);
            emailService.sendEmail("anjala050@gmail.com", "OTP Verification", "Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean verifyOtp(String email, String otp) {
        try {
            Optional<Otp> optionalOtp = otpRepository.findByEmail(email);
            if (optionalOtp.isEmpty()) {
                System.out.println("Otp not found in db");
                return false;
            }
            Otp otpEntity = optionalOtp.get();
            if (!otpEntity.getOtp().equals(otp)) {
                return false;
            }
            if (otpEntity.getExpiry_time().isBefore(LocalDateTime.now())) {
                return false;
            }
            otpRepository.delete(otpEntity);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
