package com.glow.user.application.api.model;

import com.glow.user.domain.model.Address;
import com.glow.user.domain.model.CourierAvailability;
import com.glow.user.domain.model.Permission;
import com.glow.user.domain.model.VehicleType;

import java.util.List;

public record UpdateUserRequest(
    String name,
    String phoneNumber,
    String email,
    List<Permission> permissions,
    List<Address> savedAddresses,
    VehicleType vehicleType,
    CourierAvailability courierAvailability,
    String restaurantId) {
}
