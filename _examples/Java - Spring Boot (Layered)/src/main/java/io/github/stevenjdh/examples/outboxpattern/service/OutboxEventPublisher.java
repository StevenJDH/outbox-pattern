/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stevenjdh.examples.outboxpattern.mapper.OutboxEventMapper;
import io.github.stevenjdh.examples.outboxpattern.event.abstraction.OutboxEvent;
import io.github.stevenjdh.examples.outboxpattern.repository.OutboxEventJpaRepository;
import io.github.stevenjdh.examples.outboxpattern.repository.entity.OutboxEventEntity;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

@Service
public class OutboxEventPublisher {

    private final OutboxEventJpaRepository repository;
    private final OutboxEventMapper eventMapper;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(OutboxEventJpaRepository repository,
                                OutboxEventMapper eventMapper,
                                ObjectMapper objectMapper) {
        
        this.repository = repository;
        this.eventMapper = eventMapper;
        this.objectMapper = objectMapper;
    }

    @Observed(name = "outboxpattern.event.repository", contextualName = "saveEvent")
    public void fire(OutboxEvent<?, ?> event) {
        OutboxEventEntity entity = eventMapper.toEntity(event, objectMapper);
        repository.save(entity);
    }
}
