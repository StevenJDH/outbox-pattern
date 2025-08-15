/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEvent;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.event.OrderCreatedExportedEvent;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.exception.PayloadSerializationException;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ExportedEventMapper {

    @Mapping(target = "aggregateId", expression = "java(event.getAggregateId().toString())")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "payload", expression = "java(serializePayload(event.getPayload(), objectMapper))")
    @Mapping(target = "additionalFieldValues", ignore = true)
    OrderCreatedExportedEvent toExportedEvent(OutboxEvent<?, ?> event, @Context ObjectMapper objectMapper);
    
    default String serializePayload(Object payload, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new PayloadSerializationException("Failed to serialize payload.", ex);
        }
    }
}
