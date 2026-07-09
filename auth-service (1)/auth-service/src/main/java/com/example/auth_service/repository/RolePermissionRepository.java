package com.example.auth_service.repository;

import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission,Long> {
    List<RolePermission> findByRole(Role role);

    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
