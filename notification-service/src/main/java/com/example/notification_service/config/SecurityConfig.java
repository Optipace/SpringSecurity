package com.example.notification_service.config;

import lombok.AllArgsConstructor;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
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
                .authorizeHttpRequests(notification->notification
                        .requestMatchers("/ws","/ws/**")
                        .permitAll()
//                        .requestMatchers("/auth/profile").authenticated()
//                        .requestMatchers("/users/register").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST,"/admin/users").hasAuthority("CREATE_USER")
//                        .requestMatchers(HttpMethod.DELETE,"/admin/users/{id}").hasAuthority("DELETE_USER")
//                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}").hasAuthority("UPDATE_USER")
//                        .requestMatchers(HttpMethod.GET,"/users/profile").authenticated()
//                        .requestMatchers(HttpMethod.GET,"/admin/users/all").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET,"/admin/users/{id}").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT,"/admin/{id}/revoke-admin").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}/block").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PATCH,"/admin/users/{id}/unblock").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/notifications/send").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
