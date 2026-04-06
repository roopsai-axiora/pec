package com.axiora.pec.user.auth;

import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatedUserPrincipalTest {

    @Test
    void shouldCreatePrincipalFromUserAndExposeSecurityDetails() {
        User user = User.builder()
                .id(7L)
                .fullName("Jane Employee")
                .email("jane.employee@axiora.com")
                .password("encoded")
                .role(Role.EMPLOYEE)
                .active(true)
                .build();

        AuthenticatedUserPrincipal principal =
                AuthenticatedUserPrincipal.from(user);

        assertEquals(7L, principal.id());
        assertEquals("Jane Employee", principal.fullName());
        assertEquals("jane.employee@axiora.com", principal.email());
        assertEquals(Role.EMPLOYEE, principal.role());
        assertTrue(principal.active());
        assertEquals("jane.employee@axiora.com", principal.getUsername());
        assertEquals("", principal.getPassword());
        assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")),
                principal.getAuthorities()
        );
        assertTrue(principal.isAccountNonExpired());
        assertTrue(principal.isAccountNonLocked());
        assertTrue(principal.isCredentialsNonExpired());
        assertTrue(principal.isEnabled());
    }

    @Test
    void shouldReflectDisabledUserState() {
        AuthenticatedUserPrincipal principal =
                new AuthenticatedUserPrincipal(
                        9L,
                        "Disabled User",
                        "disabled@axiora.com",
                        Role.MANAGER,
                        false
                );

        assertFalse(principal.isEnabled());
        assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_MANAGER")),
                principal.getAuthorities()
        );
    }
}
