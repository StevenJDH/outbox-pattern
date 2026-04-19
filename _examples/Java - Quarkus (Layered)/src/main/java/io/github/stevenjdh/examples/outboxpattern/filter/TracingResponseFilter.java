/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.filter;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TracingResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        var spanContext = Span.current().getSpanContext();

        if (spanContext.isValid()) {
            // Add trace identifiers.
            responseContext.getHeaders().add("trace-id", spanContext.getTraceId());
            responseContext.getHeaders().add("span-id", spanContext.getSpanId());
            responseContext.getHeaders().add("trace-flags", spanContext.getTraceFlags().asHex());

            // Add W3C traceparent.
            String traceparent = String.format("00-%s-%s-%s",
                    spanContext.getTraceId(),
                    spanContext.getSpanId(),
                    spanContext.getTraceFlags().asHex());
            responseContext.getHeaders().add("traceparent", traceparent);
        }

        var baggage = Baggage.current();
        var baggageHeader = new StringBuilder();
        
        baggage.forEach((key, entry) -> {
            if (baggageHeader.length() > 0) {
                baggageHeader.append(",");
            }
            // Collect baggage into one header (spec format).
            baggageHeader.append(key).append("=").append(entry.getValue());
            // Also, add baggage as response headers (e.g., X-Baggage-* prefix) for convenience.
            responseContext.getHeaders().add("X-Baggage-" + key, entry.getValue());
        });

        if (baggageHeader.length() > 0) {
            responseContext.getHeaders().add("baggage", baggageHeader.toString());
        }
    }
}
