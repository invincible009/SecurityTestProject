package com.sdl.web;

import com.sdl.dto.ApiResponse;
import com.sdl.dto.Mapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/public")
public class PublicController {
    private final HealthEndpoint healthEndpoint;
    private final Mapper mapper = new Mapper();

    public PublicController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health(HttpServletRequest request) {
        HealthDescriptor health = healthEndpoint.health();
        return mapper.toApiResponse(health, request);
    }

    @GetMapping("/health/liveness")
    public ResponseEntity<ApiResponse> liveness(HttpServletRequest request) {
        HealthDescriptor health = healthEndpoint.healthForPath("liveness");
        return mapper.toApiResponse(health, request);
    }

    @GetMapping("health/readiness")
    public ResponseEntity<ApiResponse> readiness(HttpServletRequest request) {
        HealthDescriptor health = healthEndpoint.healthForPath("readiness");
        return mapper.toApiResponse(health, request);
    }
}
