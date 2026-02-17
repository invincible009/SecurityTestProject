package com.sdl.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class SecurityAppAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LocalDateTime currentTime = LocalDateTime.now();
        String message = (accessDeniedException.getMessage() != null) ? accessDeniedException.getMessage() : "Authorization  Failed";
        String path = request.getRequestURI();
        response.setHeader("Security-Applicaion-Error", "Authorization Failed");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String jsonMessage = String.format("{\"timestamp\":\"%s\",\"Status\":\"%s\", \"error\":\"%s\", \"message\":\"%s\", \"path\":\"%s\"}",
                currentTime, HttpServletResponse.SC_FORBIDDEN, "Forbidden", message, path);

        response.getWriter().write(jsonMessage);

    }
}
