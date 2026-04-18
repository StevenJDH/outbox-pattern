/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.api.rest.exception;

import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.ErrorResponseDTO;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Singleton
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Response UNPROCESSABLE_ENTITY = Response.status(422).entity("Unprocessable Entity").build();
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());
    
    @Context
    private ContainerRequestContext request;
    
    @Override
    public Response toResponse(Throwable ex) {
        switch (ex) {
            case NotFoundException nfe -> {
                int statusCode = Response.Status.NOT_FOUND.getStatusCode();
                String errorReason = Response.Status.NOT_FOUND.getReasonPhrase();
                String message = "The requested resource was not found";
                LOG.error("HTTP Code [{}] - {}: [{} {}]",
                        statusCode, message, request.getMethod(), request.getUriInfo().getRequestUri().getPath());
                
                return buildErrorResponse(statusCode, errorReason, message + ".");
            }
            case IllegalArgumentException iae -> {
                int statusCode = UNPROCESSABLE_ENTITY.getStatus();
                String errorReason = UNPROCESSABLE_ENTITY.getEntity().toString();
                String message = iae.getMessage();
                LOG.error("HTTP Code [{}] - {}", statusCode, message);
                
                return buildErrorResponse(statusCode, errorReason, message);
            }
            case WebApplicationException wae -> {
                // Covers HTTP exceptions thrown manually via `Response.status(...).build()`
                int statusCode = wae.getResponse().getStatus();
                String errorReason = wae.getResponse().getStatusInfo().getReasonPhrase();
                String message = wae.getMessage();
                LOG.error("HTTP Code [{}] - {}", statusCode, message);
                
                return buildErrorResponse(statusCode, errorReason, message);
            }
            default -> {
                int statusCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
                String errorReason = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
                String message = "Unexpected server error";
                LOG.error("HTTP Code [{}] - {}", statusCode, ex.getMessage(), ex);
                
                return buildErrorResponse(statusCode, errorReason, message);
            }
        }
    }
    
    private Response buildErrorResponse(int statusCode, String errorReason, String message) {
        var errorResponse = new ErrorResponseDTO(
                OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                statusCode,
                errorReason,
                request.getUriInfo().getPath(),
                message,
                null
        );

        return Response.status(statusCode)
                .entity(errorResponse)
                .type("application/problem+json")
                .build();
    }
}
