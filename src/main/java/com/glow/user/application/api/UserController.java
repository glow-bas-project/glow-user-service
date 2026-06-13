package com.glow.user.application.api;

import com.glow.user.application.api.model.CreateUserRequest;
import com.glow.user.application.api.model.FindUserIdsRequest;
import com.glow.user.application.api.model.FindUserIdsResponse;
import com.glow.user.application.api.model.MaterialiseUsersByIdsRequest;
import com.glow.user.application.api.model.ProfileSyncRequest;
import com.glow.user.application.api.model.UpdateUserRequest;
import com.glow.user.application.model.UserDto;
import com.glow.user.application.services.UserService;
import com.glow.user.domain.shared.GlowRoles;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService service;
    private final JsonWebToken jwt;
    private final SecurityIdentity identity;

    public UserController(UserService service, JsonWebToken jwt, SecurityIdentity identity) {
        this.service = service;
        this.jwt = jwt;
        this.identity = identity;
    }

    @GET
    @PermitAll
    public RestResponse<String> root() {
        return RestResponse.ok("glow-user-service is running");
    }

    @GET
    @Path("me")
    @Authenticated
    public RestResponse<UserDto> getMe() {
        return RestResponse.ok(service.findByKeycloakId(jwt.getSubject()));
    }

    @POST
    @Path("sync-profile")
    @Authenticated
    public RestResponse<UserDto> syncProfile(ProfileSyncRequest request) {
        return RestResponse.ok(service.syncUserProfile(jwt.getSubject(), resolveEmailClaim(), request));
    }

    private String resolveEmailClaim() {
        Object email = jwt.getClaim("email");
        if (email != null && !email.toString().isBlank()) {
            return email.toString();
        }

        Object preferredUsername = jwt.getClaim("preferred_username");
        if (preferredUsername != null && !preferredUsername.toString().isBlank()) {
            return preferredUsername.toString();
        }

        return null;
    }

    @POST
    @RolesAllowed(GlowRoles.SYSADMIN)
    public RestResponse<UserDto> createUser(CreateUserRequest request) {
        return RestResponse.ok(service.createUser(request, jwt.getSubject()));
    }

    @GET
    @Path("{id}")
    @RolesAllowed(GlowRoles.SYSADMIN)
    public RestResponse<UserDto> findById(@PathParam("id") String id) {
        return RestResponse.ok(service.findById(id));
    }

    @PUT
    @Path("{id}")
    @Authenticated
    public RestResponse<UserDto> updateById(@PathParam("id") String id, UpdateUserRequest request) {
        return RestResponse.ok(service.updateUser(id, request, jwt.getSubject(), identity.hasRole(GlowRoles.SYSADMIN)));
    }

    @POST
    @Path("find")
    @RolesAllowed(GlowRoles.SYSADMIN)
    public RestResponse<FindUserIdsResponse> findUsers(FindUserIdsRequest request) {
        var result = service.findUserIds(request.size(), request.page());

        return RestResponse.ok(new FindUserIdsResponse(
            result.result(),
            result.nextPage(),
            !result.lastPage()));
    }

    @POST
    @Path("materialise")
    @RolesAllowed(GlowRoles.SYSADMIN)
    public RestResponse<List<UserDto>> materialiseUsers(MaterialiseUsersByIdsRequest request) {
        return RestResponse.ok(service.materialise(request.ids()));
    }

    @DELETE
    @Path("{id}")
    @Authenticated
    public RestResponse<Void> deleteById(@PathParam("id") String id) {
        service.deleteUserById(id, jwt.getSubject(), identity.hasRole(GlowRoles.SYSADMIN));
        return RestResponse.noContent();
    }
}