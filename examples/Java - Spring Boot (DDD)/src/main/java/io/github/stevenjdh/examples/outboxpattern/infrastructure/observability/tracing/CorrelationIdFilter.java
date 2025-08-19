/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.observability.tracing;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import io.opentelemetry.api.trace.Span;
import java.io.IOException;

@Component
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var req = (HttpServletRequest) request;
        var resp = (HttpServletResponse) response;
        String correlationId = req.getHeader(CORRELATION_ID);

        if (correlationId != null && !correlationId.isBlank()) {
            Span.current().setAttribute("client.correlation_id", correlationId);
            resp.addHeader(CORRELATION_ID, correlationId);
            // Put into MDC so it shows up in logs.
            MDC.put(CORRELATION_ID, correlationId);
        }

        try {
            chain.doFilter(req, resp);
        } finally {
            // Cleanup MDC at the end of the request life cycle.
            MDC.remove(CORRELATION_ID);
        }
    }
}
