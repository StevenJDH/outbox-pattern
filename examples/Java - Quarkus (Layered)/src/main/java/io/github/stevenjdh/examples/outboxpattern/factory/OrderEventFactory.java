/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.factory;

import io.github.stevenjdh.examples.outboxpattern.model.Order;
import io.github.stevenjdh.examples.outboxpattern.event.OrderCreatedEvent;

public class OrderEventFactory {

    private OrderEventFactory() {
    }
    
    public static OrderCreatedEvent toOrderCreatedEvent(Order order) {
        return new OrderCreatedEvent(order.getId(), order, order.getOrderDate());
    }
}
