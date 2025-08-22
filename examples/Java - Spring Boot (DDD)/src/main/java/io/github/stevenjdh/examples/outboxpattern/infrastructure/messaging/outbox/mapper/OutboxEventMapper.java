/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEvent;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.entity.OutboxEventEntity;
import io.opentelemetry.api.baggage.Baggage;
import java.util.UUID;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = UUID.class)
public interface OutboxEventMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "aggregateId", expression = "java(((UUID) event.getAggregateId()).toString())")
    @Mapping(target = "payload", expression = "java(objectMapper.valueToTree(event.getPayload()))")
    @Mapping(target = "tracingSpanContext", expression = "java(getBaggage())")
    OutboxEventEntity toEntity(OutboxEvent<?, ?> event, @Context ObjectMapper objectMapper);
    
    default String getBaggage() {
        var baggage = Baggage.current();
        var baggageHeader = new StringBuilder();

        baggage.forEach((key, entry) -> {
            if (baggageHeader.length() > 0) {
                baggageHeader.append(",");
            }
            baggageHeader.append(key).append("=").append(entry.getValue());
        });      
        
        if (baggageHeader.length() > 0) {
            // Format: baggage=key1=val1,key2=val2.
            return String.format("baggage=%s", baggageHeader.toString());
        }
        return null;
    }
}
