package com.axiora.pec.user.dto;

import com.axiora.pec.user.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(

        @NotBlank
        String fullName,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        Role role
){}