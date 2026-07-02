package tn.iteam.meduserservice.services.impls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tn.iteam.meduserservice.models.Role;
import tn.iteam.meduserservice.models.UserEntity;
import tn.iteam.meduserservice.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsernameShouldReturnEnabledUserDetailsWhenUserIsActive() {
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(userRepository);
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@medback.com")
                .password("encoded-password")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.of(2026, 7, 2, 15, 0))
                .isActive(Boolean.TRUE)
                .build();

        when(userRepository.findByEmail("john@medback.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john@medback.com");

        assertEquals("john@medback.com", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsernameShouldReturnDisabledUserDetailsWhenUserIsInactive() {
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(userRepository);
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("inactive@medback.com")
                .password("encoded-password")
                .role(Role.PATIENT)
                .isActive(Boolean.FALSE)
                .build();

        when(userRepository.findByEmail("inactive@medback.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("inactive@medback.com");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(userRepository);
        when(userRepository.findByEmail("missing@medback.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@medback.com")
        );

        assertEquals("User not found with email: missing@medback.com", exception.getMessage());
    }
}
