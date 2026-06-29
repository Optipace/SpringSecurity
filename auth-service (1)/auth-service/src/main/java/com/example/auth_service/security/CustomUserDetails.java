package com.example.auth_service.security;

import com.example.auth_service.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private final User user;
    public CustomUserDetails(User user) {
        this.user = user;
    }
    @Override
    public String getUsername(){
        return user.getUsername();
    }

    @Override
    public String getUsername(){
        return user.getUsername();
    }
}
