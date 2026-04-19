/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.event;

import io.github.stevenjdh.examples.outboxpattern.model.Order;
import io.github.stevenjdh.examples.outboxpattern.event.abstraction.OutboxEvent;
import java.time.Instant;
import java.util.UUID;

public class OrderCreatedEvent implements OutboxEvent<Order, UUID> {

    private static final String AGGREGATE_TYPE = "Order";
    private final UUID aggregateId;
    private static final String EVENT_TYPE = "OrderCreated";
    private final Order payload;
    private final Instant timestamp;

    public OrderCreatedEvent(UUID aggregateId, Order payload, Instant timestamp) {
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    @Override
    public String getAggregateType() {
        return AGGREGATE_TYPE;
    }

    @Override
    public UUID getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getType() {
        return EVENT_TYPE;
    }

    @Override
    public Order getPayload() {
        return payload;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
