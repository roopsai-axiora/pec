package com.axiora.pec.user.domain;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void shouldReturnCorrectAuthorities() {
        User user = User.builder()
                .id(1L)
                .fullName("Roop Sai")
                .email("roop@axiora.com")
                .password("password")
                .role(Role.ADMIN)
                .build();

        Collection<? extends GrantedAuthority> authorities =
                user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN",
                authorities.iterator().next()
                        .getAuthority());
    }

    @Test
    void shouldReturnEmailAsUsername() {
        User user = User.builder()
                .email("roop@axiora.com")
                .role(Role.ADMIN)
                .build();

        assertEquals("roop@axiora.com",
                user.getUsername());
    }

    @Test
    void shouldBeEnabledWhenActive() {
        User user = User.builder()
                .email("roop@axiora.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        assertTrue(user.isEnabled());
    }

    @Test
    void shouldBeDisabledWhenInactive() {
        User user = User.builder()
                .email("roop@axiora.com")
                .role(Role.ADMIN)
                .active(false)
                .build();

        assertFalse(user.isEnabled());
    }

    @Test
    void shouldReturnTrueForAccountNonExpired() {
        User user = User.builder()
                .email("roop@axiora.com")
                .role(Role.ADMIN)
                .build();

        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void shouldReturnTrueForAccountNonLocked() {
        User user = User.builder()
                .email("roop@axiora.com")
                .role(Role.ADMIN)
                .build();

        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void shouldReturnTrueForCredentialsNonExpired() {
        User user = User.builder()
                .email("roop@axiora.com")
                .role(Role.ADMIN)
                .build();

        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void shouldReturnCorrectAuthoritiesForEmployee() {
        User user = User.builder()
                .email("employee@axiora.com")
                .role(Role.EMPLOYEE)
                .build();

        Collection<? extends GrantedAuthority> authorities =
                user.getAuthorities();

        assertEquals("ROLE_EMPLOYEE",
                authorities.iterator().next()
                        .getAuthority());
    }

    @Test
    void shouldReturnCorrectAuthoritiesForManager() {
        User user = User.builder()
                .email("manager@axiora.com")
                .role(Role.MANAGER)
                .build();

        Collection<? extends GrantedAuthority> authorities =
                user.getAuthorities();

        assertEquals("ROLE_MANAGER",
                authorities.iterator().next()
                        .getAuthority());
    }
}