package com.example.user_service.config;

import com.example.user_service.filter.JWTFilter;
import lombok.AllArgsConstructor;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final JWTFilter jwtFilter;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RateLimitFilter rateLimitFilter(){
        return new RateLimitFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, RateLimitFilter rateLimitFilter) throws Exception{
        httpSecurity
                .cors(cors->{})
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(user->user
//                        .requestMatchers("/auth/register","/auth/login","/auth/refresh","/auth/verify-otp","/ws","/ws/**")
//                        .permitAll()
//                        .requestMatchers("/auth/profile").authenticated()
                        .requestMatchers(HttpMethod.POST,"/admin/users").hasAuthority("CREATE_USER")
                        .requestMatchers(HttpMethod.DELETE,"/admin/users/{id}").hasAuthority("DELETE_USER")
                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}").hasAuthority("UPDATE_USER")
                        .requestMatchers(HttpMethod.GET,"/admin/users/{id}").hasAuthority("VIEW_USER")
                        .requestMatchers(HttpMethod.GET,"/admin/users/all").hasAuthority("VIEW_USER")
                        .requestMatchers(HttpMethod.GET,"/users/profile").hasAuthority("VIEW_PROFILE")
                        .requestMatchers(HttpMethod.PUT,"/admin/{id}/revoke-admin").hasAuthority("REVOKE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}/block").hasAuthority("BLOCK_USER")
                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}/unblock").hasAuthority("UNBLOCK_USER")
                        .requestMatchers(HttpMethod.POST,"/admin/permissions").hasAuthority("UPDATE_PERMISSION")
                        .requestMatchers(HttpMethod.POST,"/admin/roles/{roleId}/permissions/{permissionId}").hasAuthority("UPDATE_PERMISSION")
                        .requestMatchers(HttpMethod.DELETE,"/admin/roles/{roleId}/permissions/{permissionId}").hasAuthority("UPDATE_PERMISSION")
                        .requestMatchers(HttpMethod.POST,"/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/username/{username}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/email/{email}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/internal/users/{id}").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
