package com.example.auth_service.util;

import com.example.auth_service.service.AuditService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class JWTUtil{
    private final AuditService auditService;
    private static final long ACCESS_TOKEN_EXPIRATION=15*60*1000;
    private final String SECRET_KEY="mysscretkeymysecretkeymysecretkey123456";
    private static final Logger logger= LoggerFactory.getLogger(AuditService.class);
    public String generateToken(String username){
        logger.info("Access token generated");
        auditService.save(username,"ACCESS TOKEN GENERATION","Access token generated");
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token,UserDetails userDetails){
        String username=extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    private boolean isTokenExpired(String token){
        Date expirationDate=extractExpiration(token);
        Date currentDate=new Date();
        return expirationDate.before(currentDate);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}
