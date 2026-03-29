package com.glow.user.domain.model;

import com.glow.user.domain.shared.DomainPrecondition;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final UUID keycloakId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final List<Permission> permissions;

    protected User(Builder builder) {
        this.id = Objects.isNull(builder.id) ? UUID.randomUUID() : builder.id;
        this.createdAt = Objects.isNull(builder.createdAt) ? Instant.now() : builder.createdAt;
        this.updatedAt = Objects.isNull(builder.updatedAt) ? Instant.now() : builder.updatedAt;
        this.keycloakId = DomainPrecondition.requireNonNull(builder.keycloakId,
            "User keycloakId cannot be null");
        this.name = DomainPrecondition.requireNonBlank(builder.name,
            "User name cannot be null or empty");
        this.email = DomainPrecondition.requireNonBlank(builder.email,
            "User email cannot be null or empty");
        this.phoneNumber = builder.phoneNumber;
        this.permissions = Objects.isNull(builder.permissions) ? List.of() : builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UUID getKeycloakId() {
        return keycloakId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private UUID id;
        private Instant createdAt;
        private Instant updatedAt;
        private UUID keycloakId;
        private String name;
        private String email;
        private String phoneNumber;
        private List<Permission> permissions;

        public User build() {
            return new User(this);
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder keycloakId(UUID keycloakId) {
            this.keycloakId = keycloakId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder permissions(List<Permission> permissions) {
            this.permissions = permissions;
            return this;
        }

        public UUID getId() {
            return id;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Instant getUpdatedAt() {
            return updatedAt;
        }

        public UUID getKeycloakId() {
            return keycloakId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public List<Permission> getPermissions() {
            return permissions;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setKeycloakId(UUID keycloakId) {
            this.keycloakId = keycloakId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setPermissions(List<Permission> permissions) {
            this.permissions = permissions;
        }
    }
}