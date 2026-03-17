package com.axiora.pec.user.service;

import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        User testUser = User.builder()
                .id(1L)
                .fullName("Roop Sai")
                .email("roop@axiora.com")
                .password("hashedPassword")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail("roop@axiora.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService
                .loadUserByUsername("roop@axiora.com");

        assertNotNull(userDetails);
        assertEquals("roop@axiora.com",
                userDetails.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService
                        .loadUserByUsername("notfound@axiora.com"));
    }
}
