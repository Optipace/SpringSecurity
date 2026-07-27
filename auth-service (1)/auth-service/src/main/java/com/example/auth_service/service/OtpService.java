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
    //    private final AuditService auditService;
    private final RefreshTokenRepository refreshTokenRepository;
    //    private final RoleRepository roleRepository;
//    private final UserRoleRepository userRoleRepository;
//    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    @Transactional
    public void sendOtp(String email) {
        try {
            System.out.println("inside send otp");
            String otp = String.valueOf(100000 + new Random().nextInt(900000));
            System.out.println("otp: " + otp);
//        Optional<Otp> existingOtp = otpRepository.findByEmail(email);
            otpRepository.deleteByEmail(email);
            Otp otpEntity = new Otp();
//        if (existingOtp.isPresent()) {
//            otpEntity = existingOtp.get();
//            otpEntity.setOtp(otp);
//            otpEntity.setExpiry_time(LocalDateTime.now().plusMinutes(5));
//        } else {
            otpEntity.setEmail(email);
            otpEntity.setOtp(otp);
            otpEntity.setExpiry_time(LocalDateTime.now().plusMinutes(15));
            System.out.println("otp entity: " + otpEntity);
//        }
            otpRepository.save(otpEntity);
            System.out.println("saved email: " + otpEntity.getEmail());
            System.out.println("saved otp: " + otpEntity.getOtp());
            System.out.println("Before sending email");
            emailService.sendEmail("anjala050@gmail.com", "OTP Verification", "Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes");
            System.out.println("after sending email");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean verifyOtp(String email, String otp) {
        try {
            System.out.println("starting this line");
            System.out.println("Email from request: " + email);
            System.out.println("OTP from request: " + otp);
            Optional<Otp> optionalOtp = otpRepository.findByEmail(email);
            System.out.println("optionalOtp: " + optionalOtp);
            if (optionalOtp.isEmpty()) {
                System.out.println("Otp not found in db");
                return false;
            }
            Otp otpEntity = optionalOtp.get();
            System.out.println("Otp in db: "+otpEntity.getOtp());
            System.out.println("otp entity: " + otpEntity);
            if (!otpEntity.getOtp().equals(otp)) {
                System.out.println("Otp mismatch");
                return false;
            }
            if (otpEntity.getExpiry_time().isBefore(LocalDateTime.now())) {
                System.out.println("otp expired");
                return false;
            }
            otpRepository.delete(otpEntity);
            System.out.println("reached this line");
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
