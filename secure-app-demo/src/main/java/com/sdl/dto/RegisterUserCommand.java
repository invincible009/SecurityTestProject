package com.sdl.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserCommand(
        @NotBlank(message = " UserName is required")
        String username,
        @NotBlank(message = "Password is required")
        String password,
        @NotBlank(message = "Email is required")
        String email) {
}
