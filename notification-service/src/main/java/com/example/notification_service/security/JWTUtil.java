package com.example.notification_service.security;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
    private final String SECRET_KEY=System.getenv("JWT_SECRET");
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
    public Long extractUserId(String token){
        Claims claims=Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.get("userId",Long.class);
    }

    public Long extractRoleId(String token){
        Claims claims=Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.get("roleId",Long.class);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }
}
