package com.glow.user.infrastructure.repository.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_users")
@PrimaryKeyJoinColumn(name = "id")
public class CustomerUserJpaEntity extends UserJpaEntity {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "customer_addresses",
        joinColumns = @JoinColumn(name = "user_id"))
    private List<AddressEntity> savedAddresses;

    public CustomerUserJpaEntity() {
    }

    public List<AddressEntity> getSavedAddresses() {
        return savedAddresses;
    }

    public void setSavedAddresses(List<AddressEntity> savedAddresses) {
        this.savedAddresses = savedAddresses == null ? new ArrayList<>() : new ArrayList<>(savedAddresses);
    }
}