package com.example.auth_service.config;

import com.example.auth_service.security.JWTFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final JWTFilter jwtFilter;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/auth/register","/auth/login","/auth/refresh","/auth/verify-otp")
                        .permitAll()
                        .requestMatchers("/auth/profile").authenticated()
                        .requestMatchers("/users/register").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/admin/users").hasAuthority("CREATE_USER")
                        .requestMatchers(HttpMethod.DELETE,"/admin/users/{id}").hasAuthority("DELETE_USER")
                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}").hasAuthority("UPDATE_USER")
                        .requestMatchers(HttpMethod.GET,"/users/profile").authenticated()
                        .requestMatchers(HttpMethod.GET,"/admin/users/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/admin/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/admin/{id}/revoke-admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}/block").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}/unblock").hasRole("ADMIN")
                        .anyRequest().authenticated())
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
