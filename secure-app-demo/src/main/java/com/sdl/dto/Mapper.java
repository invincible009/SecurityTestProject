package com.sdl.dto;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class Mapper {

    public ResponseEntity<ApiResponse> toApiResponse(HealthDescriptor health, HttpServletRequest request) {
        String statusCode = health != null ? health.getStatus().getCode(): "UNKNOWN";
        HttpStatus httpStatus = mapHealthToHttpStatus(statusCode);

        ApiResponse body = ApiResponse.builder()
                .message(statusCode)
                .data(health)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(httpStatus).body(body);
    }

    private HttpStatus mapHealthToHttpStatus(String actuatorStatus) {
        return "UP".equalsIgnoreCase(actuatorStatus) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
    }
}
