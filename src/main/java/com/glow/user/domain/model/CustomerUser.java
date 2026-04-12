package com.glow.user.domain.model;

import java.util.List;
import java.util.Objects;

public class CustomerUser extends User {

    private final List<Address> savedAddresses;

    private CustomerUser(Builder builder) {
        super(builder);
        this.savedAddresses = Objects.isNull(builder.savedAddresses)
            ? List.of() : builder.savedAddresses;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Address> getSavedAddresses() {
        return savedAddresses;
    }

    public static class Builder extends User.Builder {
        private List<Address> savedAddresses;

        @Override
        public CustomerUser build() { return new CustomerUser(this); }

        public Builder savedAddresses(List<Address> savedAddresses) {
            this.savedAddresses = savedAddresses;
            return this;
        }

        public List<Address> getSavedAddresses() {
            return savedAddresses;
        }
        
        public void setSavedAddresses(List<Address> savedAddresses) {
            this.savedAddresses = savedAddresses;
        }
    }
}