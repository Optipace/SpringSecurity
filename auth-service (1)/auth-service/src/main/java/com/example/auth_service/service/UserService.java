package com.example.auth_service.service;

import com.example.auth_service.dto.NotificationMessage;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.dto.UpdateRequest;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.entity.*;
import com.example.auth_service.enums.UserStatusEnum;
import com.example.auth_service.repository.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final NotificationService notificationService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private static final Logger logger= LoggerFactory.getLogger(AuditService.class);

    public User addUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setRole(registerRequest.getRole());
        userRepository.save(user);
        System.out.println("User got saved" + user.getId());
        Role role = roleRepository.findByRoleName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        System.out.println("sending web socket notification");
        NotificationMessage notification = new NotificationMessage(user.getId(), "New user", "User" + user.getUsername() + "has been registered");
        notificationService.sendNotifications(notification);
        System.out.println("notification sent");
        logger.info("User added successfully",user.getUsername());
        auditService.save(user.getUsername(),"ADD USER","Added user");
        return user;
    }

    public UserResponse getUser(Long id) {
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setStatus(user.getStatus());
        userResponse.setRole(user.getRole());
        return userResponse;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users=userRepository.findAll();
        return users.stream().map(user->
        {
            UserResponse userResponse=new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setEmail(user.getEmail());
            userResponse.setStatus(user.getStatus());
            userResponse.setRole(user.getRole());
            return userResponse;
        }).toList();
    }

    public UserResponse updateUser(Long id, UpdateRequest updateRequest) {
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("No user found"));
        if(updateRequest.getUsername()!=null){
            user.setUsername(updateRequest.getUsername());
        }
        if(updateRequest.getEmail()!=null){
            user.setEmail(updateRequest.getEmail());
        }
        if(updateRequest.getPassword()!=null){
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            logger.info("Password changed successfully",updateRequest.getPassword());
            auditService.save(user.getUsername(),"PASSWORD_CHANGE","User changed password");
        }
        if(updateRequest.getRole()!=null){
            user.setRole(updateRequest.getRole());
            System.out.println("user set"+user.getRole());
            UserRole userRole=userRoleRepository.findByUserUsername(user.getUsername()).orElseThrow(()->new RuntimeException("User role not found"));
            Role role=roleRepository.findByRoleName(updateRequest.getRole()).orElseThrow(()->new RuntimeException("Role not found"));
            userRole.setRole(role);
            userRoleRepository.save(userRole);
            logger.info("Role changed successfully",updateRequest.getRole());
            auditService.save(user.getUsername(),"ROLE_CHANGE","Changed role of user");
        }
        userRepository.save(user);
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setStatus(user.getStatus());
        userResponse.setRole(user.getRole());
        logger.info("User updated successfully",userResponse.getUsername());
        auditService.save(userResponse.getUsername(),"UPDATE USER","Updated user");
        return userResponse;
    }

    public void deleteUser(Long id) {
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        UserRole userRole=userRoleRepository.findByUserUsername(user.getUsername()).orElseThrow(()->new RuntimeException("User role not found"));
        userRoleRepository.delete(userRole);
        userRepository.delete(user);
    }

    public UserResponse getProfile(String name) {
        User user=userRepository.findByUsername(name).orElseThrow(()->new RuntimeException("User not found"));
        UserRole userRole=userRoleRepository.findByUserUsername(name).orElseThrow(()->new RuntimeException("Role not found"));
        Role role=userRole.getRole();
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setStatus(user.getStatus());
        userResponse.setRole(role.getRoleName());
        System.out.println("user response: "+userResponse);
        return userResponse;
    }

    public String revokeAdminRole(Long id) {
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        UserRole userRole=userRoleRepository.findById(id).orElseThrow(()->new RuntimeException("User role not found"));
        Role role=roleRepository.findByRoleName("USER").orElseThrow(()->new RuntimeException("Role not found"));
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        user.setRole(role.getRoleName());
        userRepository.save(user);
        return "Admin Role Revoked Successfully";
    }

    public String blockUser(Long id){
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        user.setBlocked(true);
        userRepository.save(user);
        logger.warn("USER BLOCKED : {}",user.getUsername());
        return "User Blocked Successfully";
    }

    public String unblockUser(Long id){
        User user=userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        user.setBlocked(false);
        userRepository.save(user);
        return "User Unblocked Successfully";
    }

    public void createPermission(Permission permission) {
        permissionRepository.save(permission);
    }

    public void assignPermission(Long roleId, Long permissionId) {
        Role role=roleRepository.findById(roleId).orElseThrow();
        Permission permission=permissionRepository.findById(permissionId).orElseThrow();
        RolePermission rolePermission=new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);
    }

    public void deletePermission(Long roleId,Long permissionId) {
        RolePermission rolePermission=rolePermissionRepository.findByRoleIdAndPermissionId(roleId,permissionId).orElseThrow();
        rolePermissionRepository.delete(rolePermission);
    }
}