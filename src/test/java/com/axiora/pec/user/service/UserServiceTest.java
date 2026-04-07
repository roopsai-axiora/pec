package com.axiora.pec.user.service;

import com.axiora.pec.audit.AuditService;
import com.axiora.pec.common.exception.EmailAlreadyExistsException;
import com.axiora.pec.goal.repository.GoalRepository;
import com.axiora.pec.user.auth.AuthCacheService;
import com.axiora.pec.user.auth.JwtUtil;
import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.dto.LoginRequest;
import com.axiora.pec.user.dto.RegisterRequest;
import com.axiora.pec.user.dto.AuthResponse;
import com.axiora.pec.user.dto.UserSummaryResponse;
import com.axiora.pec.user.mapper.UserMapper;
import com.axiora.pec.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuditService auditService;

    @Mock
    private AuthCacheService authCacheService;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Roop Sai")
                .email("roop@axiora.com")
                .password("hashedPassword")
                .role(Role.ADMIN)
                .build();

        registerRequest = new RegisterRequest(
                "Roop Sai",
                "roop@axiora.com",
                "password123",
                Role.ADMIN
        );

        loginRequest = new LoginRequest(
                "roop@axiora.com",
                "password123"
        );

        lenient().when(userMapper.toAuthResponse(any()))
                .thenReturn(new AuthResponse(
                        null,
                        "roop@axiora.com",
                        "ADMIN"
                ));
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(any()))
                .thenReturn(false);
        when(passwordEncoder.encode(any()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any()))
                .thenReturn(testUser);
        when(jwtUtil.generateToken(any()))
                .thenReturn("token123");

        AuthResponse response =
                userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("token123", response.token());
        assertEquals("roop@axiora.com", response.email());
        assertEquals("ADMIN", response.role());
        verify(userRepository, times(1)).save(any());
        verify(authCacheService, times(1)).put(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(any()))
                .thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.register(registerRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        "roop@axiora.com", "password123"));
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(any()))
                .thenReturn("token123");

        AuthResponse response =
                userService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token123", response.token());
        assertEquals("roop@axiora.com", response.email());
        verify(authCacheService, times(1)).put(testUser);
    }

    @Test
    void shouldDeactivateUser() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any()))
                .thenReturn(testUser);

        userService.deactivate(1L);

        assertFalse(testUser.isActive());
        verify(userRepository, times(1)).save(testUser);
        verify(authCacheService, times(1)).evictByEmail("roop@axiora.com");
    }

    @Test
    void shouldGetActiveEmployeesWithoutSearch() {
        User employee = User.builder()
                .id(3L)
                .fullName("Jane Employee")
                .email("jane.employee@axiora.com")
                .password("hashedPassword")
                .role(Role.EMPLOYEE)
                .active(true)
                .build();
        when(userRepository.findByRoleAndActiveTrueOrderByFullNameAsc(Role.EMPLOYEE))
                .thenReturn(List.of(employee));

        List<UserSummaryResponse> responses = userService.getEmployees(null, 99L, true);

        assertEquals(1, responses.size());
        assertEquals("Jane Employee", responses.getFirst().fullName());
        assertEquals("EMPLOYEE", responses.getFirst().role());
    }

    @Test
    void shouldSearchActiveEmployees() {
        User employee = User.builder()
                .id(4L)
                .fullName("John Employee")
                .email("john.employee@axiora.com")
                .password("hashedPassword")
                .role(Role.EMPLOYEE)
                .active(true)
                .build();
        when(userRepository.searchActiveUsersByRole(Role.EMPLOYEE, "john"))
                .thenReturn(List.of(employee));

        List<UserSummaryResponse> responses = userService.getEmployees(" john ", 99L, true);

        assertEquals(1, responses.size());
        assertEquals("john.employee@axiora.com", responses.getFirst().email());
        verify(userRepository).searchActiveUsersByRole(Role.EMPLOYEE, "john");
    }

    @Test
    void shouldGetOnlyEmployeesAssignedToManager() {
        User employee = User.builder()
                .id(5L)
                .fullName("Scoped Employee")
                .email("scoped.employee@axiora.com")
                .password("hashedPassword")
                .role(Role.EMPLOYEE)
                .active(true)
                .build();
        when(goalRepository.findDistinctActiveEmployeesByCreatedByIdOrderByAssignedToFullNameAsc(7L))
                .thenReturn(List.of(employee));

        List<UserSummaryResponse> responses = userService.getEmployees(null, 7L, false);

        assertEquals(1, responses.size());
        assertEquals(5L, responses.getFirst().id());
        verify(goalRepository).findDistinctActiveEmployeesByCreatedByIdOrderByAssignedToFullNameAsc(7L);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldSearchOnlyEmployeesAssignedToManager() {
        User employee = User.builder()
                .id(6L)
                .fullName("Scoped John")
                .email("scoped.john@axiora.com")
                .password("hashedPassword")
                .role(Role.EMPLOYEE)
                .active(true)
                .build();
        when(goalRepository.searchDistinctActiveEmployeesByCreatedById(8L, "john"))
                .thenReturn(List.of(employee));

        List<UserSummaryResponse> responses = userService.getEmployees(" john ", 8L, false);

        assertEquals(1, responses.size());
        assertEquals("Scoped John", responses.getFirst().fullName());
        verify(goalRepository).searchDistinctActiveEmployeesByCreatedById(8L, "john");
        verifyNoInteractions(userRepository);
    }
}
