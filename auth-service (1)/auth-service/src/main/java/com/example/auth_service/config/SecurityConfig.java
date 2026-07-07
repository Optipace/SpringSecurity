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
                        .requestMatchers(HttpMethod.POST,"/users").hasAuthority("CREATE_USER")
                        .requestMatchers(HttpMethod.DELETE,"/users/**").hasAuthority("DELETE_USER")
                        .requestMatchers(HttpMethod.PATCH,"/users/**").hasAuthority("UPDATE_USER")
                        .requestMatchers(HttpMethod.GET,"/users/profile").authenticated()
                        .requestMatchers(HttpMethod.GET,"/users/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/users/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
