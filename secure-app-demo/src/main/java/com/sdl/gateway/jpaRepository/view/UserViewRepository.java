package com.sdl.gateway.jpaRepository.view;

import com.sdl.application.model.views.UserView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserViewRepository extends JpaRepository<UserView, Long> {
    Optional<UserView> findByUsername(String username);
    Page<UserView> findAll(Pageable pageable);
}
