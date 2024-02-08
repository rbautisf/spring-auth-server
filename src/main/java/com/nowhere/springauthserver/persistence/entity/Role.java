package com.nowhere.springauthserver.persistence.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private RoleType type;

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
        return type == role.type;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public enum RoleType {
        ADMIN,
        USER;
    }

    public static class Builder {
        private UUID id;
        private RoleType type;

        public static  Builder builder() {
            return new Builder();
        }


        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder type(RoleType type) {
            this.type = type;
            return this;
        }

        public Role build() {
            Role role = new Role();
            role.setId(id);
            role.setType(type);
            return role;
        }

    }
}
