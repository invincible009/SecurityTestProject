package com.sdl.application.security;


import com.sdl.gateway.jpaRepository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApplicationUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> {
                    Set<GrantedAuthority> authorities = user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                            .collect(Collectors.toSet());

                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
