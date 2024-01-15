package com.nowhere.springauthserver.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "auth_user_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public enum RoleType {
        ADMIN,
        USER
    }
}
