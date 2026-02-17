package com.sdl.application;


import com.sdl.application.security.ApplicationTokenProvider;
import com.sdl.dto.LoginCommand;
import com.sdl.dto.LoginResult;
import com.sdl.dto.RegisterUserCommand;
import com.sdl.gateway.DemoAppGateWay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(ManagementUseCase.class)
class ApplicationUseCaseTest {

    @Autowired
    private ManagementUseCase managementUseCase;

    @MockitoBean
    private DemoAppGateWay gateWay;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private ApplicationTokenProvider tokenProvider;

    @Test
    void loginReturnsJwtForUsernamePasswordAuthentication() {
        LoginCommand command = new LoginCommand("app_user", "password");
        Authentication authenticated = new UsernamePasswordAuthenticationToken(
                "app_user",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(gateWay.existsByUserName("app_user")).thenReturn(true);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated);
        when(tokenProvider.createToken(authenticated)).thenReturn("jwt-token");

        LoginResult result = managementUseCase.login(command);

        assertEquals("jwt-token", result.accessToken());
    }

    @Test
    void loginFailsForUnknownUsername() {
        LoginCommand command = new LoginCommand("unknown_user", "password");
        when(gateWay.existsByUserName("unknown_user")).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> managementUseCase.login(command));
    }

    @Test
    void registerCreatesUserWithEncodedPassword() {
        RegisterUserCommand command = new RegisterUserCommand("new_user", "password", "new_user@mail.com");
        when(gateWay.existsByEmail("new_user@mail.com")).thenReturn(false);

        String result = managementUseCase.registerUser(command);

        assertEquals("Registered Successfully", result);
        verify(gateWay).saveUser(any());
    }

}