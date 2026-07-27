package com.example.user_service.dto;

import com.example.user_service.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRefreshResponse {
    private Role role;
}
