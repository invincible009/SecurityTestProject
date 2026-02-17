package com.sdl.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class RequestValidationBeforeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            header = header.trim();
            if (StringUtils.startsWithIgnoreCase(header, "Basic ")) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Basic authentication is disabled. Use /api/onboarding/login with username/password.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
