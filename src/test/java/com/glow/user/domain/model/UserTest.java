package com.glow.user.domain.model;

import com.glow.user.domain.shared.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @ParameterizedTest
    @NullAndEmptySource
    public void build_rejectsBlankName(String name) {
        // given
        var keycloakId = UUID.randomUUID();
        var builder = User.builder()
            .name(name)
            .email("user@example.com")
            .phoneNumber("+1234567890")
            .keycloakId(keycloakId);

        // when
        var ex = assertThrows(DomainException.class, builder::build);

        // then
        assertEquals("User name cannot be null or empty", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void build_rejectsBlankEmail(String email) {
        // given
        var keycloakId = UUID.randomUUID();
        var builder = User.builder()
            .name("John Doe")
            .email(email)
            .phoneNumber("+1234567890")
            .keycloakId(keycloakId);

        // when
        var ex = assertThrows(DomainException.class, builder::build);

        // then
        assertEquals("User email cannot be null or empty", ex.getMessage());
    }

    @Test
    public void build_rejectsNullKeycloakId() {
        // given
        var builder = User.builder()
            .name("John Doe")
            .email("user@example.com")
            .phoneNumber("+1234567890")
            .keycloakId(null);

        // when
        var ex = assertThrows(DomainException.class, builder::build);

        // then
        assertEquals("User keycloakId cannot be null", ex.getMessage());
    }

    @Test
    public void build_generatesIdAndTimestamps() {
        // given
        var keycloakId = UUID.randomUUID();
        var builder = User.builder()
            .name("John Doe")
            .email("user@example.com")
            .phoneNumber("+1234567890")
            .keycloakId(keycloakId);

        // when
        var user = builder.build();

        // then
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    public void build_defaultsPermissionsToEmpty() {
        // given
        var keycloakId = UUID.randomUUID();
        var builder = User.builder()
            .name("John Doe")
            .email("user@example.com")
            .keycloakId(keycloakId);

        // when
        var user = builder.build();

        // then
        assertEquals(List.of(), user.getPermissions());
    }

    @Test
    public void customerUserBuild_defaultsAddressesToEmpty() {
        // given
        var keycloakId = UUID.randomUUID();
        var builder = CustomerUser.builder()
            .name("John Doe")
            .email("user@example.com")
            .keycloakId(keycloakId);

        // when
        var user = (CustomerUser) builder.build();

        // then
        assertEquals(List.of(), user.getSavedAddresses());
    }

    @Test
    public void courierUserBuild_rejectsNullVehicleType() {
        // given
        var keycloakId = UUID.randomUUID();
        CourierUser.Builder builder = CourierUser.builder();
        builder.name("John Doe");
        builder.email("courier@example.com");
        builder.keycloakId(keycloakId);
        builder.vehicleType(null);

        // when
        var ex = assertThrows(DomainException.class, builder::build);

        // then
        assertEquals("Courier vehicle type cannot be null", ex.getMessage());
    }

    @Test
    public void courierUserBuild_acceptsValidCourierWithAvailability() {
        // given
        var keycloakId = UUID.randomUUID();
        CourierUser.Builder builder = CourierUser.builder();
        builder.name("John Doe");
        builder.email("courier@example.com");
        builder.keycloakId(keycloakId);
        builder.vehicleType(VehicleType.CAR);
        builder.courierAvailability(CourierAvailability.AVAILABLE);

        // when
        var user = (CourierUser) builder.build();

        // then
        assertEquals(VehicleType.CAR, user.getVehicleType());
        assertEquals(CourierAvailability.AVAILABLE, user.getCourierAvailability());
    }

    @Test
    public void restaurantUserBuild_acceptsRestaurantId() {
        // given
        var keycloakId = UUID.randomUUID();
        var restaurantId = UUID.randomUUID();
        RestaurantUser.Builder builder = RestaurantUser.builder();
        builder.name("Manager");
        builder.email("manager@example.com");
        builder.keycloakId(keycloakId);
        builder.restaurantId(restaurantId);

        // when
        var user = (RestaurantUser) builder.build();

        // then
        assertEquals(restaurantId, user.getRestaurantId());
    }
}
