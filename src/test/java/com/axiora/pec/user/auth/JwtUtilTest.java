package com.axiora.pec.user.auth;

import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "3f8c2a1d9e4b7f6a2c5d8e1f4a7b0c3d6e9f2a5b8c1d4e7f0a3b6c9d2e5f8a1");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs",
                86400000L);

        testUser = User.builder()
                .id(1L)
                .fullName("Roop Sai")
                .email("roop@axiora.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtUtil.generateToken(testUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtUtil.generateToken(testUser);
        String username = jwtUtil.extractUsername(token);
        assertEquals("roop@axiora.com", username);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtUtil.generateToken(testUser);
        assertTrue(jwtUtil.isTokenValid(token, testUser));
    }

    @Test
    void shouldInvalidateTokenForWrongUser() {
        String token = jwtUtil.generateToken(testUser);

        User anotherUser = User.builder()
                .id(2L)
                .email("another@axiora.com")
                .password("password")
                .role(Role.EMPLOYEE)
                .build();

        assertFalse(jwtUtil.isTokenValid(token, anotherUser));
    }
}