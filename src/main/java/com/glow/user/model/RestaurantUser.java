package com.glow.user.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "restaurant_users")
public class RestaurantUser extends User {

    public UUID restaurantId;
}