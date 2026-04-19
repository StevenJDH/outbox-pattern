/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.domain.order.value;

import io.github.stevenjdh.examples.outboxpattern.domain.shared.base.ValueObject;
import java.util.Objects;

public class OrderItem implements ValueObject<OrderItem> {

    private final String name;
    private final int quantity;

    public OrderItem(String name, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean sameValueAs(OrderItem other) {
        return other != null && name.equalsIgnoreCase(other.getName()) &&
                quantity == other.getQuantity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OrderItem other)) {
            return false;
        }
        return sameValueAs(other);
    }
}
