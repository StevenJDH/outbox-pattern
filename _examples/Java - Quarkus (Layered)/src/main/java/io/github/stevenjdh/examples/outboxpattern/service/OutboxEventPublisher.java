/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.outbox.quarkus.ExportedEvent;
import io.github.stevenjdh.examples.outboxpattern.event.OutboxExportedEvent;
import io.github.stevenjdh.examples.outboxpattern.mapper.ExportedEventMapper;
import io.github.stevenjdh.examples.outboxpattern.event.abstraction.OutboxEvent;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class OutboxEventPublisher {

    private final Event<ExportedEvent<?, ?>> eventPublisher;
    private final ExportedEventMapper eventMapper;
    private final ObjectMapper objectMapper;
    
    @Inject
    public OutboxEventPublisher(Event<ExportedEvent<?, ?>> eventPublisher,
                                ExportedEventMapper eventMapper,
                                ObjectMapper objectMapper) {
        
        this.eventPublisher = eventPublisher;
        this.eventMapper = eventMapper;
        this.objectMapper = objectMapper;
    }

    @Timed(value = "outboxpattern.event.repository", extraTags = {"contextual.name", "saveEvent"})
    public void fire(OutboxEvent<?, ?> event) {
        OutboxExportedEvent exportedEvent = eventMapper.toExportedEvent(event, objectMapper);
        eventPublisher.fire(exportedEvent);
    }
}
