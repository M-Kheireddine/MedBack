package tn.iteam.meduserservice.filters;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import tn.iteam.meduserservice.services.impls.CustomUserDetailsService;
import tn.iteam.meduserservice.services.impls.JwtService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, customUserDetailsService);
    }

    @Test
    void doFilterInternalShouldSkipAuthenticationWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractEmail(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void doFilterInternalShouldAuthenticateWhenTokenIsValid() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        UserDetails userDetails = new User("doctor@medback.com", "ignored", List.of());

        when(jwtService.extractEmail("valid-token")).thenReturn("doctor@medback.com");
        when(customUserDetailsService.loadUserByUsername("doctor@medback.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("doctor@medback.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternalShouldNotOverrideExistingAuthentication() throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("existing-user", null, List.of())
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtService.extractEmail("valid-token")).thenReturn("doctor@medback.com");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(customUserDetailsService, never()).loadUserByUsername("doctor@medback.com");
        assertEquals("existing-user", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
