package com.sdl.gateway.jpaRepository;

import com.sdl.application.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByName(String name);
}
