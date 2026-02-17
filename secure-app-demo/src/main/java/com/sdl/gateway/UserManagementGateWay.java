package com.sdl.gateway;

import com.sdl.application.model.User;

import com.sdl.application.model.views.UserView;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserManagementGateWay {
    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    List<User> findAllUsers();

    User saveUser(User user);

    void deleteUserById(Long id);


    boolean existsByEmail(@NotBlank(message = "Email is required") String email);

    boolean existsByUserName(@NotBlank(message = "UserName is required") String username);

    UserView getUserViewByUsername(String name);

    Page<UserView> findSystemUsers(Pageable pageRequest);
}
