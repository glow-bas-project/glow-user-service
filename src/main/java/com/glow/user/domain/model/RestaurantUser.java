package com.glow.user.domain.model;

import java.util.UUID;

public class RestaurantUser extends User {

    private final UUID restaurantId;

    private RestaurantUser(Builder builder) {
        super(builder);
        this.restaurantId = builder.restaurantId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public static class Builder extends User.Builder {
        private UUID restaurantId;

        @Override
        public RestaurantUser build() { return new RestaurantUser(this); }

        public Builder restaurantId(UUID restaurantId) {
            this.restaurantId = restaurantId; return this;
        }

        public UUID getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(UUID restaurantId) {
            this.restaurantId = restaurantId;
        }
    }
}