package com.glow.user.infrastructure.filters;

import com.glow.user.application.services.UserService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
                userService.ensureUserExists(keycloakSubject, email);
            }
        } catch (Exception e) {
            LOG.warn("Failed to auto-provision user from JWT: {}", e.getMessage());
        }
    }
}
