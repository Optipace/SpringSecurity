//package com.example.auth_service.security;
//
//import com.example.auth_service.entity.Role;
//import com.example.auth_service.entity.RolePermission;
//import com.example.auth_service.entity.User;
//import com.example.auth_service.entity.UserRole;
//import com.example.auth_service.repository.RolePermissionRepository;
//import com.example.auth_service.repository.RoleRepository;
//import com.example.auth_service.repository.UserRepository;
//import com.example.auth_service.repository.UserRoleRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@AllArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//    private final UserRoleRepository userRoleRepository;
//    private final RolePermissionRepository rolePermissionRepository;
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
//        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
//        if(user.isBlocked()){
//            throw new DisabledException("User account is blocked");
//        }
//        UserRole userRole=userRoleRepository.findByUserUsername(user.getUsername()).orElseThrow(()->new RuntimeException("User not found"));
//        Role role=userRole.getRole();
//        List<RolePermission> rolePermissions=rolePermissionRepository.findByRole(role);
//        System.out.println("Role: "+role.getRoleName());
//        List<GrantedAuthority> authorities=new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleName()));
//        rolePermissions.forEach(rolePermission -> authorities.add(new SimpleGrantedAuthority(rolePermission.getPermission().getPermissionName())));
//        System.out.println("authorities: "+authorities);
//        System.out.println("permission count: "+rolePermissions.size());
//        for(RolePermission rolePermission:rolePermissions){
//            System.out.println(rolePermission.getPermission().getPermissionName());
//        }
//        return new CustomUserDetails(user,authorities);
//    }
//}
