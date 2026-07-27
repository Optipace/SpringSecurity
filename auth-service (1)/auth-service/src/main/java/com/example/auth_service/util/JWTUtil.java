package com.example.auth_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class JWTUtil{
    private static final long ACCESS_TOKEN_EXPIRATION=15*60*1000;
    private static final Logger logger= LoggerFactory.getLogger(JWTUtil.class);
    private String SECRET_KEY=System.getenv("JWT_SECRET");

    public String generateToken(Long userId,Long roleId){
        return Jwts.builder()
                .claim("userId",userId)
                .claim("roleId",roleId)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Long userId, LocalDateTime issuedTime){
        System.out.println("inside generate refresh token");
        return Jwts.builder()
                .claim("userId",userId)
                .claim("issuedTime",issuedTime.toString())
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    public Long extractUserId(String token){
        Claims claims=Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.get("userId",Long.class);
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        }
        catch(Exception exception){
            return false;
        }
    }
}
