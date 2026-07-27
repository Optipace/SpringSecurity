package com.example.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class JWTUtil {
    private static final long ACCESS_TOKEN_EXPIRATION=15*60*1000;
    private final String SECRET_KEY=System.getenv("JWT_SECRET");
    public String generateToken(Long userId,Long roleId){
        return Jwts.builder()
                .claim("userId",userId)
                .claim("roleId",roleId)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Long userId, LocalDateTime issuedTime){
        return Jwts.builder()
                .claim("userId",userId)
                .claim("issuedTime",issuedTime.toString())
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public Long extractUserId(String token){
        Claims claims=Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.get("userId",Long.class);
    }

    public Long extractRoleId(String token){
        Claims claims=Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.get("roleId",Long.class);
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
