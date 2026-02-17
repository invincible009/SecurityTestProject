package com.sdl.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LoginResult(String accessToken,
                          LocalDateTime issuedAt,
                          LocalDateTime expiresAt
) {}
