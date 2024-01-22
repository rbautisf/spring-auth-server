package com.nowhere.springauthserver.persistence.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private RoleType type;

    @ManyToMany(fetch = FetchType.LAZY ,mappedBy = "roles")
    private List<AuthUser> users;

    public List<AuthUser> getUsers() {
        return users;
    }

    public void setUsers(List<AuthUser> users) {
        this.users = users;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RoleType getType() {
        return type;
    }

    public void setType(RoleType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (!id.equals(role.id)) return false;
        if (type != role.type) return false;
        return Objects.equals(users, role.users);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }

    public enum RoleType {
        ADMIN,
        USER;
        public static RoleType fromString(String role) {
            return RoleType.valueOf(role.toUpperCase());
        }
    }
}
