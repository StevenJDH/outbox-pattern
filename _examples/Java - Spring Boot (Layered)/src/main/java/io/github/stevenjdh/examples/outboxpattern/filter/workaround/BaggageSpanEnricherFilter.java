/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.filter.workaround;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Currently there is a bug in the framework that doesn't add baggage tag fields to span attributes.
 * This implements a workaround to handle this functionality. If DEBUG logging is enabled, ignore
 * # the log entry 'Will propagate new baggage context for entries {}'.
 * Reference (Maybe): https://github.com/micrometer-metrics/tracing/issues/933
 */
@Component
public class BaggageSpanEnricherFilter implements Filter {
    
    private final List<String> tagFields;
    private static final Logger LOG = LoggerFactory.getLogger(BaggageSpanEnricherFilter.class.getName());

    public BaggageSpanEnricherFilter(@Value("${management.tracing.baggage.tag-fields:[]}")List<String> tagFields) {
        this.tagFields = tagFields;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        var span = Span.current();
        var baggage = Baggage.current();
        List<String> tags = new ArrayList<>();
        
        // Copies baggage into span attributes so services like Zipkin show them.
        baggage.forEach((key, entry) -> {
            if (tagFields.contains(key)) {
                span.setAttribute("baggage." + key, entry.getValue());
                tags.add(String.format("%s=%s", key, entry.getValue()));
            }          
        });
        
        LOG.info("Will propagate new baggage context for entries {}", tags);
        
        chain.doFilter(request, response);
    }
}
