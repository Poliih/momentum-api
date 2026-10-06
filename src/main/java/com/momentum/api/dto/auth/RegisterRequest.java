package com.momentum.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "O nome e obrigatorio") @Size(max = 120) String name,
        @NotBlank(message = "O email e obrigatorio") @Email(message = "Email invalido") String email,
        @NotBlank(message = "A senha e obrigatoria") @Size(min = 8, message = "A senha deve ter no minimo 8 caracteres") String password
) {}
