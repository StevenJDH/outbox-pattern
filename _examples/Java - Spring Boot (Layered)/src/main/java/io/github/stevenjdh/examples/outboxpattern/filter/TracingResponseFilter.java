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
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class TracingResponseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var httpResponse = (HttpServletResponse) response;
        var spanContext = Span.current().getSpanContext();

        if (spanContext.isValid()) {
            // Add trace identifiers.
            httpResponse.addHeader("trace-id", spanContext.getTraceId());
            httpResponse.addHeader("span-id", spanContext.getSpanId());
            httpResponse.addHeader("trace-flags", spanContext.getTraceFlags().asHex());

            // Add W3C traceparent.
            String traceparent = String.format("00-%s-%s-%s",
                    spanContext.getTraceId(),
                    spanContext.getSpanId(),
                    spanContext.getTraceFlags().asHex());
            httpResponse.addHeader("traceparent", traceparent);
        }

        var baggage = Baggage.current();
        var baggageHeader = new StringBuilder();

        baggage.forEach((key, entry) -> {
            if (baggageHeader.length() > 0) {
                baggageHeader.append(",");
            }
            // Collect baggage into one header (spec format).
            baggageHeader.append(key).append("=").append(entry.getValue());
            // Also, add baggage as response headers (with X-Baggage-* prefix).
            httpResponse.addHeader("X-Baggage-" + key, entry.getValue());
        });

        if (baggageHeader.length() > 0) {
            httpResponse.addHeader("baggage", baggageHeader.toString());
        }

        chain.doFilter(request, httpResponse);
    }
}
