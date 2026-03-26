package com.glow.user.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {

    @GET
    @Path("/me")
    @RolesAllowed("CUSTOMER")
    @Produces(MediaType.APPLICATION_JSON)
    public String me() {
        return "{\"message\": \"Keycloak integration working\"}";
    }
}