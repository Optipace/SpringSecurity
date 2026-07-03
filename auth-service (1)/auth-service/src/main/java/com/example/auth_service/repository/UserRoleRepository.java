package com.example.auth_service.repository;

import com.example.auth_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    Optional<UserRole> findByUserUsername(String username);
}
