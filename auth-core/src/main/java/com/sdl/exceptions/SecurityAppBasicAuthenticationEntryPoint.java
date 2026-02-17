package com.sdl.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

public class SecurityAppBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LocalDateTime currentTime = LocalDateTime.now();
        String message = (authException.getMessage() != null) ? authException.getMessage() : "Unauthorized";
        String path = request.getRequestURI();
        response.setHeader("Security-Application-Error", "Authentication Failed");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String jsonMessage = String.format("{\"timestamp\":\"%s\",\"status\":\"%s\", \"error\":\"%s\", \"message\":\"%s\", \"path\":\"%s\"}",
                currentTime, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", message, path);

        response.getWriter().write(jsonMessage);
    }
}
