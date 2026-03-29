package com.glow.user.infrastructure.repository.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "courier_users")
@PrimaryKeyJoinColumn(name = "id")
public class CourierUserJpaEntity extends UserJpaEntity {

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "stripe_account_id")
    private String stripeAccountId;

    @Column(name = "stripe_onboarding_complete")
    private Boolean stripeOnboardingComplete;

    public CourierUserJpaEntity() {
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }

    public Boolean getStripeOnboardingComplete() {
        return stripeOnboardingComplete;
    }

    public void setStripeOnboardingComplete(Boolean b) {
        this.stripeOnboardingComplete = b;
    }
}