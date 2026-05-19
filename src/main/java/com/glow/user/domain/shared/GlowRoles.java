package com.glow.user.domain.shared;

/**
 * Central class for all Keycloak role names used.
 */
public final class GlowRoles {
    public static final String CUSTOMER = "CUSTOMER";
    public static final String COURIER = "COURIER";
    public static final String RESTAURANT_USER = "RESTAURANT_USER";
    public static final String SYSADMIN = "SYSADMIN";

    public static final String[] ALL_ROLES = {CUSTOMER, COURIER, RESTAURANT_USER, SYSADMIN};

    private GlowRoles() {
        // Utility class, not instantiable
    }
}
