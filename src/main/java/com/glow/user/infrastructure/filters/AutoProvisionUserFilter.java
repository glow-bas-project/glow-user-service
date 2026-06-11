package com.glow.user.infrastructure.filters;

import com.glow.user.application.services.UserService;
import com.glow.user.domain.model.Permission;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Provider
@Priority(1000)
public class AutoProvisionUserFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AutoProvisionUserFilter.class);

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (jwt == null || jwt.getSubject() == null) {
            return;
        }

        try {
            String keycloakSubject = jwt.getSubject();
            Object emailClaim = jwt.getClaim("email");
            String email = null;

            if (emailClaim != null && !emailClaim.toString().isBlank()) {
                email = emailClaim.toString();
            } else {
                Object preferredUsername = jwt.getClaim("preferred_username");
                if (preferredUsername != null && !preferredUsername.toString().isBlank()) {
                    email = preferredUsername.toString();
                }
            }

            if (email != null) {
                userService.ensureUserExists(keycloakSubject, email, resolvePermissions());
            }
        } catch (Exception e) {
            LOG.warn("Failed to auto-provision user from JWT: {}", e.getMessage());
        }
    }

    private List<Permission> resolvePermissions() {
        Object realmAccess = jwt.getClaim("realm_access");
        if (!(realmAccess instanceof Map<?, ?> realmAccessMap)) {
            return List.of(Permission.CUSTOMER);
        }

        Object rolesClaim = realmAccessMap.get("roles");
        if (!(rolesClaim instanceof Iterable<?> roles)) {
            return List.of(Permission.CUSTOMER);
        }

        List<Permission> permissions = new ArrayList<>();
        for (Object role : roles) {
            if (role == null) {
                continue;
            }

            try {
                permissions.add(Permission.valueOf(role.toString()));
            } catch (IllegalArgumentException ignored) {
                // Ignore Keycloak realm roles that do not map to glow-user permissions.
            }
        }

        if (permissions.isEmpty()) {
            permissions.add(Permission.CUSTOMER);
        }

        return List.copyOf(permissions);
    }
}
