package com.glow.user.application.api;

import com.glow.user.application.api.model.CreateUserRequest;
import com.glow.user.application.api.model.FindUserIdsRequest;
import com.glow.user.application.api.model.FindUserIdsResponse;
import com.glow.user.application.api.model.MaterialiseUsersByIdsRequest;
import com.glow.user.application.api.model.UpdateUserRequest;
import com.glow.user.application.model.UserDto;
import com.glow.user.application.services.UserService;
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

    public UserController(UserService service, JsonWebToken jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @GET
    @PermitAll
    public RestResponse<String> root() {
        return RestResponse.ok("glow-user-service is running");
    }

    @POST
    @RolesAllowed({"CUSTOMER", "COURIER", "RESTAURANT_USER", "SYSADMIN"})
    public RestResponse<UserDto> createUser(CreateUserRequest request) {
        return RestResponse.ok(service.createUser(request, jwt.getSubject()));
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"CUSTOMER", "COURIER", "RESTAURANT_USER", "SYSADMIN"})
    public RestResponse<UserDto> findById(@PathParam("id") String id) {
        return RestResponse.ok(service.findById(id));
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"CUSTOMER", "COURIER", "RESTAURANT_USER", "SYSADMIN"})
    public RestResponse<UserDto> updateById(@PathParam("id") String id, UpdateUserRequest request) {
        return RestResponse.ok(service.updateUser(id, request));
    }

    @POST
    @Path("find")
    @RolesAllowed({"CUSTOMER", "COURIER", "RESTAURANT_USER", "SYSADMIN"})
    public RestResponse<FindUserIdsResponse> findUsers(FindUserIdsRequest request) {
        var result = service.findUserIds(request.size(), request.page());

        return RestResponse.ok(new FindUserIdsResponse(
            result.result(),
            result.nextPage(),
            !result.lastPage()));
    }

    @POST
    @Path("materialise")
    @RolesAllowed({"CUSTOMER", "COURIER", "RESTAURANT_USER", "SYSADMIN"})
    public RestResponse<List<UserDto>> materialiseUsers(MaterialiseUsersByIdsRequest request) {
        return RestResponse.ok(service.materialise(request.ids()));
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"CUSTOMER", "COURIER", "RESTAURANT_USER", "SYSADMIN"})
    public RestResponse<Void> deleteById(@PathParam("id") String id) {
        boolean deleted = service.deleteUserById(id);
        return deleted ? RestResponse.noContent() : RestResponse.status(RestResponse.Status.NOT_FOUND);
    }
}