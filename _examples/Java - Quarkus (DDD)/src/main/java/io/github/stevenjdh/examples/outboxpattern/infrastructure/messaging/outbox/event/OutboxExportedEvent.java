/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.messaging.outbox.event;

import com.fasterxml.jackson.databind.JsonNode;
import io.debezium.outbox.quarkus.ExportedEvent;
import java.time.Instant;

public class OutboxExportedEvent implements ExportedEvent<String, JsonNode> {

    private final String aggregateType;
    private final String aggregateId;
    private final String eventType;
    private final JsonNode payload;
    private final Instant timestamp;

    public OutboxExportedEvent(String aggregateType, String aggregateId,
            String eventType, JsonNode payload, Instant timestamp) {
        
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }

    @Override
    public String getType() {
        return eventType;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public JsonNode getPayload() {
        return payload;
    }
}
