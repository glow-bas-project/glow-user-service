package com.glow.user.infrastructure.mappers;

import com.glow.user.domain.model.*;
import com.glow.user.infrastructure.repository.entities.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserEntityMapper {

    public UserJpaEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserJpaEntity entity = instantiateEntity(user);
        mapBaseToEntity(user, entity);
        return entity;
    }

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        User.Builder builder = instantiateBuilder(entity);
        mapBaseToBuilder(entity, builder);
        return builder.build();
    }

    private UserJpaEntity instantiateEntity(User user) {
        if (user instanceof CustomerUser customerUser) {
            CustomerUserJpaEntity entity = new CustomerUserJpaEntity();
            entity.setSavedAddresses(customerUser.getSavedAddresses().stream()
                .map(this::toEmbeddable)
                .toList());
            return entity;
        }

        if (user instanceof CourierUser courierUser) {
            CourierUserJpaEntity entity = new CourierUserJpaEntity();
            entity.setVehicleType(courierUser.getVehicleType() == null ? null : courierUser.getVehicleType().name());
            entity.setStripeAccountId(courierUser.getStripeAccountId());
            entity.setStripeOnboardingComplete(courierUser.getStripeOnboardingComplete());
            return entity;
        }

        if (user instanceof RestaurantUser restaurantUser) {
            RestaurantUserJpaEntity entity = new RestaurantUserJpaEntity();
            entity.setRestaurantId(restaurantUser.getRestaurantId() == null
                ? null : restaurantUser.getRestaurantId().toString());
            return entity;
        }

        return new UserJpaEntity();
    }

    private User.Builder instantiateBuilder(UserJpaEntity entity) {
        if (entity instanceof CustomerUserJpaEntity customerEntity) {
            return CustomerUser.builder().savedAddresses(
                customerEntity.getSavedAddresses() == null
                    ? List.of()
                    : customerEntity.getSavedAddresses().stream()
                        .map(this::toAddress)
                        .toList());
        }

        if (entity instanceof CourierUserJpaEntity courierEntity) {
            var vehicleType = courierEntity.getVehicleType() == null
                ? null
                : VehicleType.valueOf(courierEntity.getVehicleType());

            return CourierUser.builder()
                .vehicleType(vehicleType)
                .stripeAccountId(courierEntity.getStripeAccountId())
                .stripeOnboardingComplete(courierEntity.getStripeOnboardingComplete());
        }

        if (entity instanceof RestaurantUserJpaEntity restaurantEntity &&
            restaurantEntity.getRestaurantId() != null && !restaurantEntity.getRestaurantId().isBlank()) {
            return RestaurantUser.builder()
                .restaurantId(UUID.fromString(restaurantEntity.getRestaurantId()));
        }

        return User.builder();
    }

    private void mapBaseToEntity(User user, UserJpaEntity entity) {
        entity.setId(user.getId().toString());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        entity.setKeycloakId(user.getKeycloakId().toString());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPhoneNumber(user.getPhoneNumber());
        entity.setPermissions(permissionsToStrings(user.getPermissions()));
    }

    private void mapBaseToBuilder(UserJpaEntity entity, User.Builder builder) {
        builder.id(UUID.fromString(entity.getId()));
        builder.createdAt(entity.getCreatedAt());
        builder.updatedAt(entity.getUpdatedAt());
        builder.keycloakId(UUID.fromString(entity.getKeycloakId()));
        builder.name(entity.getName());
        builder.email(entity.getEmail());
        builder.phoneNumber(entity.getPhoneNumber());
        builder.permissions(stringsToPermissions(entity.getPermissions()));
    }

    private AddressEntity toEmbeddable(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressEntity(
            address.address(),
            address.city(),
            address.country(),
            address.longitude(),
            address.latitude());
    }

    private Address toAddress(AddressEntity embeddable) {
        if (embeddable == null) {
            return null;
        }

        return new Address(
            embeddable.getAddress(),
            embeddable.getCity(),
            embeddable.getCountry(),
            embeddable.getLongitude(),
            embeddable.getLatitude());
    }

    private List<String> permissionsToStrings(List<Permission> permissions) {
        if (permissions == null) return List.of();
        return permissions.stream().map(Enum::name).toList();
    }

    private List<Permission> stringsToPermissions(List<String> permissions) {
        if (permissions == null) return List.of();
        return permissions.stream().map(Permission::valueOf).toList();
    }
}