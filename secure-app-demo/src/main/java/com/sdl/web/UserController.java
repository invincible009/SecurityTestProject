package com.sdl.web;

import com.sdl.application.ManagementUseCase;
import com.sdl.dto.ApiResponse;
import com.sdl.application.model.views.UserView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final ManagementUseCase managementUseCase;

    public UserController(ManagementUseCase managementUseCase) {
        this.managementUseCase = managementUseCase;
    }

    @GetMapping("/me")
    public ApiResponse getUser(HttpServletRequest request) {
        UserView user = managementUseCase.viewLoginInUser(request.getUserPrincipal());
        return ApiResponse.builder()
                .data(user)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateUser(HttpServletRequest request,@PathVariable @Valid Long id) {
        return ApiResponse.builder().build();
    }

}
