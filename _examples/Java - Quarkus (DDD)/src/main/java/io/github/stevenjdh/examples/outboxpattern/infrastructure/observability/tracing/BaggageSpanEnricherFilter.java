/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.observability.tracing;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BaggageSpanEnricherFilter  implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var span = Span.current();
        var baggage = Baggage.current();
        
        // Copies baggage into span attributes so services like Zipkin show them.
        baggage.forEach((key, entry) ->
                span.setAttribute("baggage." + key, entry.getValue()));
    }
}
