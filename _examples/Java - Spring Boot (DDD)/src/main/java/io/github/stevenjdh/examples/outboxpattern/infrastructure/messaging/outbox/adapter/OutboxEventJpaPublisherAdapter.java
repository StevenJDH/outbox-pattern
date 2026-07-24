/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.adapter;

import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEvent;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEventPublisher;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.mapper.OutboxEventMapper;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.repository.OutboxEventJpaRepository;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.entity.OutboxEventEntity;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class OutboxEventJpaPublisherAdapter implements OutboxEventPublisher {

    private final OutboxEventJpaRepository repository;
    private final OutboxEventMapper eventMapper;
    private final ObjectMapper objectMapper;
    private final ObservationRegistry observationRegistry;

    public OutboxEventJpaPublisherAdapter(OutboxEventJpaRepository repository,
                                          OutboxEventMapper eventMapper,
                                          ObjectMapper objectMapper,
                                          ObservationRegistry observationRegistry) {
        
        this.repository = repository;
        this.eventMapper = eventMapper;
        this.objectMapper = objectMapper;
        this.observationRegistry = observationRegistry;
    }

    @Override
    @Observed(name = "outboxpattern.event.repository", contextualName = "saveEvent")
    public void fire(OutboxEvent<?, ?> event) {
        var current = observationRegistry.getCurrentObservation();

        // Adds low-cardinality key-value pairs to the current observation to emulate what the
        // debezium-quarkus-outbox extension does automatically. Opted for this inline approach
        // to avoid creating an ObservationContext/ObservationConvention centrally.
        if (current != null) {
            current.lowCardinalityKeyValue("aggregateId", event.getAggregateId().toString());
            current.lowCardinalityKeyValue("aggregateType", event.getAggregateType());
            current.lowCardinalityKeyValue("type", event.getType());
        }

        OutboxEventEntity entity = eventMapper.toEntity(event, objectMapper);
        repository.save(entity);
    }
}
