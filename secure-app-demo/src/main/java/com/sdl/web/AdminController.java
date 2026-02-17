package com.sdl.web;

import com.sdl.application.ApplicationUseCase;
import com.sdl.application.ManagementUseCase;
import com.sdl.dto.ApiResponse;
import com.sdl.dto.UserRecord;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {

    private final ManagementUseCase managementUseCase;
    private final ApplicationUseCase applicationUseCase;

    public AdminController(ManagementUseCase managementUseCase,
                           ApplicationUseCase applicationUseCase) {
        this.managementUseCase = managementUseCase;
        this.applicationUseCase = applicationUseCase;
    }

    @GetMapping("/users")
    public ApiResponse getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        Set<UserRecord>  users = managementUseCase.getAllRegisteredUsers();
        return ApiResponse.builder()
                .message("All users")
                .data(users)
                .build();
    }
}
