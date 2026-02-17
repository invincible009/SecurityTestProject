package com.sdl.application.security;

import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoAppAuthenticationProvider implements AuthenticationProvider {
    private final ApplicationUserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;

    public DemoAppAuthenticationProvider(ApplicationUserDetailService userDetailService,
                                         PasswordEncoder passwordEncoder) {
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = (String) authentication.getCredentials();
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        if (passwordEncoder.matches(pwd, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    username,
                    pwd,
                    userDetails.getAuthorities()
            );
        } else {
            throw new BadCredentialsException("Invalid password!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DemoAppAuthenticationProvider authenticationProvider =
                new DemoAppAuthenticationProvider(userDetailService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return  providerManager;
    }
}
