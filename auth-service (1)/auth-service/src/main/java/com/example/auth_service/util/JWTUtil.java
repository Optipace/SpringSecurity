package com.example.auth_service.util;

//import com.example.auth_service.service.AuditService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
//@AllArgsConstructor
public class JWTUtil{
//    private final AuditService auditService;
    private static final long ACCESS_TOKEN_EXPIRATION=15*60*1000;
//    private final String SECRET_KEY="mysscretkeymysecretkeymysecretkey123456";
    private static final Logger logger= LoggerFactory.getLogger(JWTUtil.class);
//    private final String SECRET_KEY=System.getenv("JWT_SECRET");
//    @Value("${JWT_SECRET:${JWT_SECRET}}")
//    private String SECRET_KEY;
    private String SECRET_KEY=System.getenv("JWT_SECRET");
//    public String generateToken(String username){
//        logger.info("Access token generated");
//        auditService.save(username,"ACCESS TOKEN GENERATION","Access token generated");
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRATION))
//                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//    }

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

//    public boolean validateToken(String token,UserDetails userDetails){
//        String username=extractUsername(token);
//        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//    }

//    public String extractUsername(String token) {
//        Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
//        return claims.getSubject();
//    }

//    private boolean isTokenExpired(String token){
//        Date expirationDate=extractExpiration(token);
//        Date currentDate=new Date();
//        return expirationDate.before(currentDate);
//    }

//    private Date extractExpiration(String token) {
//        return extractClaim(token,Claims::getExpiration);
//    }

//    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
//        Claims claims=extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }

//    private Claims extractAllClaims(String token) {
//        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
//    }

    public Long extractUserId(String token){
        Claims claims=Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.get("userId",Long.class);
    }

//    public Long extractRoleId(String token){
//        Claims claims=Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
//        return claims.get("roleId",Long.class);
//    }

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
