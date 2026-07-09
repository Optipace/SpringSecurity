package com.example.auth_service.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> cache=new ConcurrentHashMap<>();
    private Bucket createBucket(){
        Bandwidth limit=Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofMinutes(1)).build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)throws ServletException, IOException{
        String path=httpServletRequest.getRequestURI();
        System.out.println("Rate limit filter called");
        String ip=httpServletRequest.getRemoteAddr();
        if(path.equals("/auth/login") || path.equals("/auth/verify-otp")) {
            Bucket bucket = cache.computeIfAbsent(ip, k -> createBucket());
            System.out.println("ip: " + ip);
            System.out.println("available token: " + bucket.getAvailableTokens());
            if (bucket.tryConsume(1)) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } else {
                httpServletResponse.setStatus(429);
                httpServletResponse.getWriter().write("Too many requests. Please try again later.");
                return;
            }
        }
        else{
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }
    }
}
