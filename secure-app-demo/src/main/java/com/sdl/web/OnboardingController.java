package com.sdl.web;

import com.sdl.application.ManagementUseCase;
import com.sdl.dto.ApiResponse;
import com.sdl.dto.LoginCommand;
import com.sdl.dto.LoginResult;
import com.sdl.dto.RegisterUserCommand;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {

    private final ManagementUseCase managementUseCase;

    public OnboardingController(ManagementUseCase managementUseCase) {
        this.managementUseCase = managementUseCase;
    }


    @PostMapping("/register")
    public ApiResponse registerUser(
            @RequestBody @Valid RegisterUserCommand userCommand,
            HttpServletRequest request){
      requireNonNull(userCommand, "Invalid user command");
        String res = managementUseCase.registerUser(userCommand);
        return ApiResponse.builder()
                .message(res)
                .path(request.getRequestURI())
                .build();
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody @Valid LoginCommand command, HttpServletRequest request){
        requireNonNull(command, "Invalid login command");
        LoginResult loginResult = managementUseCase.login(command);
        return ApiResponse.builder()
                .data(loginResult)
                .path(request.getRequestURI())
                .build();
    }
}
