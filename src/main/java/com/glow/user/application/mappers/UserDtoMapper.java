package com.glow.user.application.mappers;

import com.glow.user.application.model.UserDto;
import com.glow.user.domain.model.CourierUser;
import com.glow.user.domain.model.CustomerUser;
import com.glow.user.domain.model.RestaurantUser;
import com.glow.user.domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserDtoMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        var savedAddresses = List.<com.glow.user.domain.model.Address>of();
        com.glow.user.domain.model.VehicleType vehicleType = null;
        String restaurantId = null;

        if (user instanceof CustomerUser customerUser) {
            savedAddresses = customerUser.getSavedAddresses();
        } else if (user instanceof CourierUser courierUser) {
            vehicleType = courierUser.getVehicleType();
        } else if (user instanceof RestaurantUser restaurantUser) {
            restaurantId = restaurantUser.getRestaurantId() == null
                ? null : restaurantUser.getRestaurantId().toString();
        }

        return new UserDto(
            user.getId().toString(),
            user.getName(),
            user.getPhoneNumber(),
            user.getEmail(),
            user.getPermissions(),
            savedAddresses,
            vehicleType,
            restaurantId);
    }

    public User toDomain(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User.Builder builder = resolveBuilder(dto);
        builder.id(UUID.fromString(dto.id()));
        builder.name(dto.name());
        builder.phoneNumber(dto.phoneNumber());
        builder.email(dto.email());
        builder.permissions(dto.permissions());

        return builder.build();
    }

    private User.Builder resolveBuilder(UserDto dto) {
        if (!dto.savedAddresses().isEmpty()) {
            return CustomerUser.builder().savedAddresses(dto.savedAddresses());
        }

        if (dto.vehicleType() != null) {
            return CourierUser.builder().vehicleType(dto.vehicleType());
        }

        if (dto.restaurantId() != null && !dto.restaurantId().isBlank()) {
            return RestaurantUser.builder().restaurantId(UUID.fromString(dto.restaurantId()));
        }

        return User.builder();
    }
}