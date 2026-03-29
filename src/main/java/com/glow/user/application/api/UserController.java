package com.glow.user.application.api;

import com.glow.user.application.api.model.CreateUserRequest;
import com.glow.user.application.api.model.FindUserIdsRequest;
import com.glow.user.application.api.model.FindUserIdsResponse;
import com.glow.user.application.api.model.MaterialiseUsersByIdsRequest;
import com.glow.user.application.model.UserDto;
import com.glow.user.application.services.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // uncomment if you want a simple health check endpoint
    @GET
    public RestResponse<String> root() {
        return RestResponse.ok("glow-user-service is running");
    }

    @POST
    public RestResponse<UserDto> createUser(CreateUserRequest request) {
        return RestResponse.ok(service.createUser(request));
    }

    @GET
    @Path("{id}")
    public RestResponse<UserDto> findById(@PathParam("id") String id) {
        return RestResponse.ok(service.findById(id));
    }

    @POST
    @Path("find")
    public RestResponse<FindUserIdsResponse> findUsers(FindUserIdsRequest request) {
        var result = service.findUserIds(request.size(), request.page());

        return RestResponse.ok(new FindUserIdsResponse(
            result.result(),
            result.nextPage(),
            result.lastPage()));
    }

    @POST
    @Path("materialise")
    public RestResponse<List<UserDto>> materialiseUsers(MaterialiseUsersByIdsRequest request) {
        return RestResponse.ok(service.materialise(request.ids()));
    }

    @DELETE
    @Path("{id}")
    public RestResponse<Void> deleteById(@PathParam("id") String id) {
        boolean deleted = service.deleteUserById(id);
        return deleted ? RestResponse.noContent() : RestResponse.status(RestResponse.Status.NOT_FOUND);
    }
}