package com.glow.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courier_users")
public class CourierUser extends User {

    public String vehicleType;
}