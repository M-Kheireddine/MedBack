package tn.iteam.meduserservice.services.impls;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.iteam.meduserservice.models.UserEntity;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${medback.security.jwt.secret}")
    private String jwtSecret;

    @Value("${medback.security.jwt.expiration-minutes:1440}")
    private long expirationMinutes;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public LocalDateTime extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }

    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId().toString());
        return generateToken(claims, user);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private String generateToken(Map<String, Object> claims, UserEntity user) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationMinutes * 60_000L);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
