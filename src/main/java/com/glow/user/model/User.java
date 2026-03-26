package com.glow.user.model;

import com.glow.common.BaseEntity;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    public UUID keycloakId;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, unique = true)
    public String email;

    public String phoneNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    public List<String> permissions;
}