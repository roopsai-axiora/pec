package com.axiora.pec.user.auth;

import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthCacheService {

    private static final Logger log = LoggerFactory.getLogger(AuthCacheService.class);

    private final UserRepository userRepository;

    public AuthCacheService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "auth-users", key = "#email")
    public AuthenticatedUserPrincipal getByEmail(String email) {
        log.info("Auth cache miss while resolving authenticated user. Loading from database.");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));
        return AuthenticatedUserPrincipal.from(user);
    }

    @CachePut(value = "auth-users", key = "#user.email")
    public AuthenticatedUserPrincipal put(User user) {
        log.info("Warming auth cache for userId={}", user.getId());
        return AuthenticatedUserPrincipal.from(user);
    }

    @CacheEvict(value = "auth-users", key = "#email")
    public void evictByEmail(String email) {
        log.info("Evicting auth cache entry for authenticated user.");
    }
}
