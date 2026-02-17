package com.sdl.application.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles = new HashSet<>();

    protected Authority() {
    }

    public Authority(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Set<Role> getRoles() { return roles; }

    public void setRoles(Set<Role> roles) { this.roles = roles; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(id, authority.id) && Objects.equals(name, authority.name) && Objects.equals(roles, authority.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, roles);
    }


}
