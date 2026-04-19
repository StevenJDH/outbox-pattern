/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEvent;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEventPublisher;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.mapper.OutboxEventMapper;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.repository.OutboxEventJpaRepository;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.entity.OutboxEventEntity;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Repository;

@Repository
public class OutboxEventJpaPublisherAdapter implements OutboxEventPublisher {

    private final OutboxEventJpaRepository repository;
    private final OutboxEventMapper eventMapper;
    private final ObjectMapper objectMapper;

    public OutboxEventJpaPublisherAdapter(OutboxEventJpaRepository repository,
                                          OutboxEventMapper eventMapper,
                                          ObjectMapper objectMapper) {
        
        this.repository = repository;
        this.eventMapper = eventMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Observed(name = "outboxpattern.event.repository", contextualName = "saveEvent")
    public void fire(OutboxEvent<?, ?> event) {
        OutboxEventEntity entity = eventMapper.toEntity(event, objectMapper);
        repository.save(entity);
    }
}
