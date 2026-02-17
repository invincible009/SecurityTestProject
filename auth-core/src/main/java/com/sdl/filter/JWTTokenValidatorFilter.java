package com.sdl.filter;

import com.sdl.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {


    private final SecretKey secretKey;

    public JWTTokenValidatorFilter(Environment environment) {
        this.secretKey = buildSecretKey(environment);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String headerValue = request.getHeader(ApplicationConstants.JWT_HEADER);

        if (!StringUtils.hasText(headerValue) || !headerValue.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = headerValue.substring("Bearer ".length()).trim();
        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            String username = String.valueOf(claims.get("username"));
            String authorities = String.valueOf(claims.get("authorities"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException ex) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Token expired");
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Invalid token");
        }

        filterChain.doFilter(request, response);
    }

    private static SecretKey buildSecretKey(Environment env) {
        // Prefer required property (no silent fallback to a weak default)
        String configured = env.getProperty(ApplicationConstants.JWT_SECRET_KEY);

        if (!StringUtils.hasText(configured)) {
            throw new IllegalStateException(
                    "JWT secret key is missing. Configure property '" + ApplicationConstants.JWT_SECRET_KEY
                            + "' with at least 32 bytes (HS256)."
            );
        }

        byte[] keyBytes = decodeSecret(configured.trim());

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret key is too short: " + keyBytes.length
                            + " bytes. It must be at least 32 bytes for HS256."
            );
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static byte[] decodeSecret(String configured) {
        // Supports either:
        // 1) base64:<BASE64_VALUE>
        // 2) a plain text secret (UTF-8 bytes)
        if (configured.regionMatches(true, 0, "base64:", 0, "base64:".length())) {
            String b64 = configured.substring("base64:".length()).trim();
            try {
                return Base64.getDecoder().decode(b64);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("JWT secret key has invalid Base64 content after 'base64:' prefix.", e);
            }
        }
        return configured.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/public/")
                || path.equals("/api/onboarding/login")
                || path.equals("/api/onboarding/register");
    }
}
