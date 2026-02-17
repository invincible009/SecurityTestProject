package com.sdl.application.model.views;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user_view")
public class UserView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "roles", columnDefinition = "jsonb")
    private String rolesJson;

    @Column(name = "authorities", columnDefinition = "jsonb")
    private String authoritiesJson;

    @Transient
    private Set<String> roles;

    @Transient
    private Set<String> authorities;


    public UserView() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getRolesJson() {
        return rolesJson;
    }

    public void setRolesJson(String rolesJson) {
        this.rolesJson = rolesJson;
    }

    public String getAuthoritiesJson() {
        return authoritiesJson;
    }

    public void setAuthoritiesJson(String authoritiesJson) {
        this.authoritiesJson = authoritiesJson;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}
