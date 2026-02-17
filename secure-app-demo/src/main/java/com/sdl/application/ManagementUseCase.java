package com.sdl.application;

import com.sdl.application.model.User;
import com.sdl.application.model.views.UserView;
import com.sdl.application.security.ApplicationTokenProvider;
import com.sdl.dto.*;
import com.sdl.gateway.DemoAppGateWay;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ManagementUseCase {

    private final PasswordEncoder passwordEncoder;
    private final DemoAppGateWay gateway;
    private final AuthenticationManager authenticationManager;
    private final ApplicationTokenProvider TokenProv;

    public ManagementUseCase(DemoAppGateWay gateway,
                             AuthenticationManager authenticationManager,
                             ApplicationTokenProvider tokenProv) {
        this.gateway = gateway;
        this.authenticationManager = authenticationManager;
        TokenProv = tokenProv;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    private boolean isUserExist(String normalizedEmail) {
        return gateway.existsByEmail(normalizedEmail);
    }

    private boolean isEmailValid(@NotBlank(message = "Email is required") String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) return false;
        return true;
    }

    private boolean isRegisteredUser(@NotBlank(message = "UserName is required") String username) {
        return gateway.existsByUserName(username);
    }


    private Set<UserRecord> retrieveUsers() {
        Pageable pageRequest = PageRequest.of(0, 10);
        Page<UserView> userPage = gateway.findSystemUsers(pageRequest);

        return userPage.getContent().stream()
                .map(user -> new UserRecord(
                        user.getUsername(),
                        user.getEmail(),
                        user.isEnabled(),
                        user.getRoles().stream().collect(Collectors.toUnmodifiableSet()),
                        user.getAuthorities().stream().collect(Collectors.toUnmodifiableSet())
                ))
                .collect(Collectors.toSet());
    }


    public String registerUser(RegisterUserCommand command){
        if(!isEmailValid(command.email())){
            return "Invalid Email Address";
        }
        String normalizedEmail = command.email().trim().toLowerCase();

        if(isUserExist(normalizedEmail)){
            return "Email is already registered in the system";
        }

        User user = new User();
        user.setUsername(command.username());
        user.setPassword(passwordEncoder.encode(command.password()));
        user.setEnabled(true);
        user.setEmail(normalizedEmail);

        gateway.saveUser(user);
        return "Registered Successfully";
    }


    public LoginResult login(LoginCommand command){
        if(!isRegisteredUser(command.username())) throw new UsernameNotFoundException("Username or Password Not Found");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.username(), command.password())
        );
        String jwt = TokenProv.createToken(authentication);
        return LoginResult.builder()
                .accessToken(jwt)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
    }

    public UserView viewLoginInUser(Principal userPrincipal) {
        return gateway.getUserViewByUsername(userPrincipal.getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Set<UserRecord> getAllRegisteredUsers() {
        return retrieveUsers();
    }

}
