package tn.iteam.meduserservice.services.impls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.models.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "medback-jwt-secret-key-for-tests-only-please-change-1234567890");
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 60L);
    }

    @Test
    void generateTokenShouldAllowEmailAndExpirationExtraction() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("doctor@medback.com")
                .role(Role.DOCTOR)
                .build();

        String token = jwtService.generateToken(user);

        assertEquals("doctor@medback.com", jwtService.extractEmail(token));
        assertTrue(jwtService.extractExpiration(token).isAfter(LocalDateTime.now()));
    }

    @Test
    void isTokenValidShouldReturnTrueWhenUsernameMatchesAndTokenIsNotExpired() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("doctor@medback.com")
                .role(Role.DOCTOR)
                .build();
        String token = jwtService.generateToken(user);

        User userDetails = new User("doctor@medback.com", "ignored", java.util.List.of());

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidShouldReturnFalseWhenUsernameDoesNotMatch() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("doctor@medback.com")
                .role(Role.DOCTOR)
                .build();
        String token = jwtService.generateToken(user);

        User userDetails = new User("other@medback.com", "ignored", java.util.List.of());

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }
}
