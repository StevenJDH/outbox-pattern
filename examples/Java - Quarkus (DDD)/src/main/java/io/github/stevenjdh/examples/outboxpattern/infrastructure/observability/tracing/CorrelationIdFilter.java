/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.observability.tracing;

import io.opentelemetry.api.trace.Span;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final MDCRequestContext mdcContext;
    private static final String CORRELATION_ID = "X-Correlation-Id";

    @Inject
    public CorrelationIdFilter(MDCRequestContext mdcContext) {
        this.mdcContext = mdcContext;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String correlationId = requestContext.getHeaderString(CORRELATION_ID);
        if (correlationId != null && !correlationId.isBlank()) {
            Span.current().setAttribute("client.correlation_id", correlationId);
            requestContext.setProperty(CORRELATION_ID, correlationId);
            // Put into MDC so it shows up in logs.
            mdcContext.put(CORRELATION_ID, correlationId);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String correlationId = (String) requestContext.getProperty(CORRELATION_ID);
        if (correlationId != null && !correlationId.isBlank()) {
            responseContext.getHeaders().add(CORRELATION_ID, correlationId);
        }
    }
}
