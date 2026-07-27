package com.example.user_service.repository;

import com.example.user_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    Optional<UserRole> findByUserUsername(String username);
}
