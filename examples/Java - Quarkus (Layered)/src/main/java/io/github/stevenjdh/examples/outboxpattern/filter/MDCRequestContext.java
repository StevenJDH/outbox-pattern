/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.filter;

import jakarta.enterprise.context.RequestScoped;
import java.util.HashMap;
import java.util.Map;
import org.jboss.logging.MDC;

/**
 * This will take care of managing the MDC for a request. For example, it adds
 * new entries, and only removes them when the request has fully completed. This
 * ensures that the values are still available in other areas that come later in
 * the request life cycle, like the access logs.
 * 
 * NOTE: This is only required in Quarkus, because Spring Boot Filters already hook
 * into the full life cycle of a request, so cleanup can happen there directly.
 */
@RequestScoped
public class MDCRequestContext implements AutoCloseable {

    private final Map<String, String> values = new HashMap<>();

    public void put(String key, String value) {
        values.put(key, value);
        MDC.put(key, value);
    }

    public String get(String key) {
        return values.get(key);
    }

    public void remove(String key) {
        values.remove(key);
        MDC.remove(key);
    }

    @Override
    public void close() {
        values.keySet().forEach(MDC::remove);
        values.clear();
    }
}
