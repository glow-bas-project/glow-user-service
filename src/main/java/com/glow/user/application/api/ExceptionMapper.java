package com.glow.user.application.api;

import com.glow.user.application.api.model.ApiError;
import com.glow.user.domain.shared.DomainException;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ExceptionMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionMapper.class);

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

    @ServerExceptionMapper
    public Response webApplicationExceptionInterceptor(WebApplicationException ex) {
        return Response.status(ex.getResponse().getStatus())
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(new ApiError(
                "REQUEST_ERROR",
                ex.getMessage(),
                ex.getResponse().getStatus(),
                Instant.now()))
            .build();
    }

    @ServerExceptionMapper
    public Response genericExceptionInterceptor(Throwable ex) {
        LOG.error("Unhandled exception while processing request", ex);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(new ApiError(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                Instant.now()))
            .build();
    }
}