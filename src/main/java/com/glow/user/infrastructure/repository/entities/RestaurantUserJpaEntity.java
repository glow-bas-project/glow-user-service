package com.glow.user.infrastructure.repository.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_users")
@PrimaryKeyJoinColumn(name = "id")
public class RestaurantUserJpaEntity extends UserJpaEntity {

    @Column(name = "restaurant_id")
    private String restaurantId;

    public RestaurantUserJpaEntity() {
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}