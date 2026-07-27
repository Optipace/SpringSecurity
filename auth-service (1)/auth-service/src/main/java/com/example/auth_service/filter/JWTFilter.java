package com.example.auth_service.filter;

import com.example.auth_service.client.UserClient;
import com.example.auth_service.dto.UserResponse;
import com.example.auth_service.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserClient userClient;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String path=httpServletRequest.getServletPath();
        if(path.equals("/auth/refresh")){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        String authHeader = httpServletRequest.getHeader("Authorization");
        System.out.println("Authorizatio header: " + authHeader);
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token: " + token);
            try {
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserResponse user=userClient.getUser(userId);
                    List<GrantedAuthority>authorities=List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
                    UserDetails userDetails=new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
                    if (jwtUtil.validateToken(token)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (ExpiredJwtException expiredJwtExpired) {
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.getWriter().write("JWT Token has expired");
                return;
            } catch (Exception exception) {
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.getWriter().write("Invalid JWT Token");
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}