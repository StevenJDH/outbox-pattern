/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.outbox.quarkus.ExportedEvent;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEvent;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEventPublisher;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.event.OutboxExportedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.mapper.ExportedEventMapper;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;

@ApplicationScoped
public class OutboxEventDebeziumPublisherAdapter implements OutboxEventPublisher {

    private final Event<ExportedEvent<?, ?>> eventPublisher;
    private final ExportedEventMapper eventMapper;
    private final ObjectMapper objectMapper;
    
    @Inject
    public OutboxEventDebeziumPublisherAdapter(Event<ExportedEvent<?, ?>> eventPublisher,
                                               ExportedEventMapper eventMapper,
                                               ObjectMapper objectMapper) {
        
        this.eventPublisher = eventPublisher;
        this.eventMapper = eventMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Timed(value = "outboxpattern.event.repository", extraTags = {"contextual.name", "saveEvent"})
    public void fire(OutboxEvent<?, ?> event) {
        OutboxExportedEvent exportedEvent = eventMapper.toExportedEvent(event, objectMapper);
        eventPublisher.fire(exportedEvent);
    }
}
