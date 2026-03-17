package com.axiora.pec.user.controller;

import com.axiora.pec.user.domain.Role;
import com.axiora.pec.user.dto.AuthResponse;
import com.axiora.pec.user.dto.LoginRequest;
import com.axiora.pec.user.dto.RegisterRequest;
import com.axiora.pec.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Roop Sai",
                "roop@axiora.com",
                "password123",
                Role.ADMIN
        );

        AuthResponse response = new AuthResponse(
                "token123",
                "roop@axiora.com",
                "ADMIN"
        );

        when(userService.register(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("token123"))
                .andExpect(jsonPath("$.email")
                        .value("roop@axiora.com"))
                .andExpect(jsonPath("$.role")
                        .value("ADMIN"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequest request = new LoginRequest(
                "roop@axiora.com",
                "password123"
        );

        AuthResponse response = new AuthResponse(
                "token123",
                "roop@axiora.com",
                "ADMIN"
        );

        when(userService.login(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("token123"));
    }

    @Test
    void shouldReturnBadRequestForInvalidRegister()
            throws Exception {
        RegisterRequest request = new RegisterRequest(
                "",
                "invalid-email",
                "",
                null
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeactivateUser() throws Exception {
        mockMvc.perform(patch("/api/auth/deactivate/1"))
                .andExpect(status().isNoContent());
    }
}