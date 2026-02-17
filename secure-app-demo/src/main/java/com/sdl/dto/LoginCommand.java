package com.sdl.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginCommand(
        @NotBlank(message = "UserName is required")
        String username,
        @NotBlank(message = "Password is required")
        String password) {
}
