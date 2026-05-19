package com.glow.user.domain.model;

import com.glow.user.domain.shared.DomainPrecondition;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CourierUser extends User {

    private final VehicleType vehicleType;
    private final CourierAvailability courierAvailability;
    private final String stripeAccountId;
    private final Boolean stripeOnboardingComplete;

    private CourierUser(Builder builder) {
        super(builder);
        this.vehicleType = DomainPrecondition.requireNonNull(builder.vehicleType,
            "Courier vehicle type cannot be null");
        this.courierAvailability = builder.courierAvailability;
        this.stripeAccountId = builder.stripeAccountId;
        this.stripeOnboardingComplete = builder.stripeOnboardingComplete;
    }

    public static Builder builder() {
        return new Builder();
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public CourierAvailability getCourierAvailability() {
        return courierAvailability;
    }

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public Boolean getStripeOnboardingComplete() {
        return stripeOnboardingComplete;
    }

    public static class Builder extends User.Builder {
        private VehicleType vehicleType;
        private CourierAvailability courierAvailability;
        private String stripeAccountId;
        private Boolean stripeOnboardingComplete;

        @Override
        public Builder id(UUID id) {
            super.id(id);
            return this;
        }

        @Override
        public Builder createdAt(Instant createdAt) {
            super.createdAt(createdAt);
            return this;
        }

        @Override
        public Builder updatedAt(Instant updatedAt) {
            super.updatedAt(updatedAt);
            return this;
        }

        @Override
        public Builder keycloakId(UUID keycloakId) {
            super.keycloakId(keycloakId);
            return this;
        }

        @Override
        public Builder name(String name) {
            super.name(name);
            return this;
        }

        @Override
        public Builder email(String email) {
            super.email(email);
            return this;
        }

        @Override
        public Builder phoneNumber(String phoneNumber) {
            super.phoneNumber(phoneNumber);
            return this;
        }

        @Override
        public Builder permissions(List<Permission> permissions) {
            super.permissions(permissions);
            return this;
        }

        @Override
        public CourierUser build() { return new CourierUser(this); }

        public Builder vehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType; return this;
        }
        public Builder courierAvailability(CourierAvailability courierAvailability) {
            this.courierAvailability = courierAvailability; return this;
        }
        public Builder stripeAccountId(String stripeAccountId) {
            this.stripeAccountId = stripeAccountId; return this;
        }
        public Builder stripeOnboardingComplete(Boolean stripeOnboardingComplete) {
            this.stripeOnboardingComplete = stripeOnboardingComplete; return this;
        }

        public VehicleType getVehicleType() {
            return vehicleType;
        }

        public CourierAvailability getCourierAvailability() {
            return courierAvailability;
        }

        public String getStripeAccountId() {
            return stripeAccountId;
        }

        public Boolean getStripeOnboardingComplete() {
            return stripeOnboardingComplete;
        }

        public void setVehicleType(VehicleType v) {
            this.vehicleType = v;
        }

        public void setCourierAvailability(CourierAvailability a) {
            this.courierAvailability = a;
        }

        public void setStripeAccountId(String s) {
            this.stripeAccountId = s;
        }

        public void setStripeOnboardingComplete(Boolean b) {
            this.stripeOnboardingComplete = b;
        }
    }
}