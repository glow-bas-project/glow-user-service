package com.glow.user.application.api;

import com.glow.user.application.api.model.ApiError;
import com.glow.user.domain.shared.DomainException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.time.Instant;

public class ExceptionMapper {

    @ServerExceptionMapper
    public Response domainExceptionInterceptor(DomainException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(new ApiError(
                "DOMAIN_ERROR",
                ex.getMessage(),
                Response.Status.BAD_REQUEST.getStatusCode(),
                Instant.now()))
            .build();
    }
}