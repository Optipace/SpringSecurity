package com.example.auth_service.repository;

import com.example.auth_service.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findByEmail(String email);

    void deleteByEmail(String email);
}
