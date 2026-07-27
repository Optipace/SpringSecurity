package com.example.user_service.filter;

import com.example.user_service.client.UserClient;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.entity.Role;
import com.example.user_service.entity.RolePermission;
import com.example.user_service.entity.User;
import com.example.user_service.repository.RolePermissionRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.security.JWTUtil;
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
//    private final CustomUserDetailsService customUserDetailsService;
    private final JWTUtil jwtUtil;
//    private final UserClient userClient;
    private final UserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");
        System.out.println("Authorizatio header: " + authHeader);
        System.out.println("Method: "+httpServletRequest.getMethod());
        System.out.println("URI: "+httpServletRequest.getRequestURI());
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token: " + token);
            try {
                Long userId = jwtUtil.extractUserId(token);

//                username = jwtUtil.extractUsername(token);
//                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UserResponse user=userClient.getUser(userId);
                    User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
                    Role role=user.getRole();
                    List<RolePermission>rolePermissions=rolePermissionRepository.findByRole(role);
                    List<SimpleGrantedAuthority> authorities=rolePermissions.stream().map(rolePermission -> new SimpleGrantedAuthority(rolePermission.getPermission().getPermissionName())).toList();
//                    authorities.add();
//                    List<GrantedAuthority> authorities=List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().getRoleName()));
//                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    UserDetails userDetails=new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
                    if (jwtUtil.validateToken(token)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        System.out.println("Authentication set: " + SecurityContextHolder.getContext().getAuthentication());
                        System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
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
        System.out.println("finished function");
        System.out.println("Response status: "+httpServletResponse.getStatus());
    }
}
