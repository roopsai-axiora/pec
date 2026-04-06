package com.axiora.pec.user.auth;

import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
class AuthCacheServiceTest {

    @Test
    void shouldLoadPrincipalFromRepository() {
        User user = User.builder()
                .id(1L)
                .fullName("John Manager")
                .email("john.manager@axiora.com")
                .password("encoded")
                .role(Role.MANAGER)
                .active(true)
                .build();
        AuthCacheService authCacheService =
                new AuthCacheService(repositoryReturning(Optional.of(user)));

        AuthenticatedUserPrincipal principal =
                authCacheService.getByEmail("john.manager@axiora.com");

        assertEquals(1L, principal.id());
        assertEquals("john.manager@axiora.com", principal.email());
        assertEquals(Role.MANAGER, principal.role());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        AuthCacheService authCacheService =
                new AuthCacheService(repositoryReturning(Optional.empty()));

        assertThrows(
                UsernameNotFoundException.class,
                () -> authCacheService.getByEmail("missing@axiora.com")
        );
    }

    @Test
    void shouldWarmPrincipalFromUser() {
        User user = User.builder()
                .id(2L)
                .fullName("Jane Employee")
                .email("jane.employee@axiora.com")
                .password("encoded")
                .role(Role.EMPLOYEE)
                .active(true)
                .build();
        AuthCacheService authCacheService =
                new AuthCacheService(repositoryReturning(Optional.empty()));

        AuthenticatedUserPrincipal principal = authCacheService.put(user);

        assertEquals(2L, principal.id());
        assertEquals("Jane Employee", principal.fullName());
        assertEquals("jane.employee@axiora.com", principal.email());
    }

    @Test
    void shouldEvictWithoutRepositoryInteraction() {
        AuthCacheService authCacheService =
                new AuthCacheService(repositoryReturning(Optional.empty()));
        authCacheService.evictByEmail("jane.employee@axiora.com");
    }

    private UserRepository repositoryReturning(Optional<User> user) {
        return (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class[]{UserRepository.class},
                (proxy, method, args) -> {
                    if ("findByEmail".equals(method.getName())) {
                        return user;
                    }
                    if ("toString".equals(method.getName())) {
                        return "TestUserRepository";
                    }
                    throw new UnsupportedOperationException(
                            "Unexpected repository method: " + method.getName()
                    );
                }
        );
    }
}
