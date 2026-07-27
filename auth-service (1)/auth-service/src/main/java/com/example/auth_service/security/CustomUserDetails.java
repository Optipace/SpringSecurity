//package com.example.auth_service.security;
//
//import com.example.auth_service.entity.RolePermission;
//import com.example.auth_service.entity.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import java.util.Collection;
//import java.util.List;
//
//public class CustomUserDetails implements UserDetails {
//    private List<GrantedAuthority> authorities;
//    private final User user;
//    public CustomUserDetails(User user, List<GrantedAuthority> authorities) {
//        this.user = user;
//        this.authorities=authorities;
//    }
//
//    @Override
//    public Collection<?extends GrantedAuthority> getAuthorities(){
//        return authorities;
//    }
//    @Override
//    public String getPassword(){
//        return user.getPassword();
//    }
//    @Override
//    public String getUsername(){
//        return user.getUsername();
//    }
//    @Override
//    public boolean isAccountNonExpired(){
//        return true;
//    }
//    @Override
//    public boolean isAccountNonLocked(){
//        return true;
//    }
//    @Override
//    public boolean isCredentialsNonExpired(){
//        return true;
//    }
////    @Override
////    public boolean isEnabled(){
////        return user.getStatus().name().equals("ACTIVE");
////    }
//}
