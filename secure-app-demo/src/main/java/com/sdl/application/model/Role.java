package com.sdl.application.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "role_authorities",
            joinColumns = @JoinColumn(name = "role_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "authority_id", nullable = false)
    )
    private Set<Authority> authorities = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    protected Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Set<Authority> getAuthorities() { return authorities; }

    public void setAuthorities(Set<Authority> authorities) { this.authorities = authorities; }

    public Set<User> getUsers() { return users; }

    public void setUsers(Set<User> users) { this.users = users; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(authorities, role.authorities) && Objects.equals(users, role.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, authorities, users);
    }
}
