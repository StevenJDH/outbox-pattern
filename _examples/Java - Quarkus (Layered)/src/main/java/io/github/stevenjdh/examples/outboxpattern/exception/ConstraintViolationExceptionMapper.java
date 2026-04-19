/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.exception;

import io.github.stevenjdh.examples.outboxpattern.dto.response.ErrorResponseDTO;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class overrides the default handler that is more specific than the global handler. 
 */
@Provider
@Singleton
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Response UNPROCESSABLE_ENTITY = Response.status(422).entity("Unprocessable Entity").build();
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());
    
    @Context
    private ContainerRequestContext request;
    
    @Override
    public Response toResponse(ConstraintViolationException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        int statusCode = UNPROCESSABLE_ENTITY.getStatus();
        String errorReason = UNPROCESSABLE_ENTITY.getEntity().toString();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            var fieldPath = violation.getPropertyPath(); // Not doing toString() to make work like Spring Boot.
            String field = null;
            
            for (var node : fieldPath) {
                // Keeps updating until we have just
                // field name without complete path.
                field = node.getName(); 
            }
            
            String error = violation.getMessage();
            validationErrors.put(field, error);
        }

        String message = "Request had validation errors";
        LOG.error("HTTP Code [{}] - {}: [{}]", statusCode, message, validationErrors);
        
        var errorResponse = new ErrorResponseDTO(
                OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                statusCode,
                errorReason,
                request.getUriInfo().getPath(),
                message + ".",
                validationErrors
        );

        return Response.status(statusCode)
                .entity(errorResponse)
                .type("application/problem+json")
                .build();
    }
}
