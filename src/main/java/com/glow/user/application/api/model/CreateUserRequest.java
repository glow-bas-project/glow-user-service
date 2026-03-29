package com.glow.user.application.api.model;

import com.glow.user.domain.model.Address;
import com.glow.user.domain.model.Permission;
import com.glow.user.domain.model.VehicleType;

import java.util.List;

public record CreateUserRequest(
    String keycloakId,
    String name,
    String phoneNumber,
    String email,
    List<Permission> permissions,
    List<Address> savedAddresses,
    VehicleType vehicleType,
    String restaurantId) {
}