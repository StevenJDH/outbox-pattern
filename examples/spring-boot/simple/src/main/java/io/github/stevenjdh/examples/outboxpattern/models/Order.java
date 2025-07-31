/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.models;

import io.github.stevenjdh.examples.outboxpattern.models.enums.OrderStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final Instant orderDate;
    private OrderStatus status;
    private final Customer customer;
    private final List<OrderItem> items;
    private int totalNumberOfItems;

    public Order(UUID id, Instant orderDate, Customer customer, List<OrderItem> items) {
        if (id == null || orderDate == null || customer == null || items == null) {
            throw new IllegalArgumentException("Invalid order parameters.");
        }
        this.id = id;
        this.orderDate = orderDate;
        this.status = OrderStatus.PLACED;
        this.customer = customer;
        this.items = items;
    }

    public UUID getId() {
        return id;
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void addItemToOrder(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        items.add(item);
    }
    
    public void ship() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Only placed orders can be shipped.");
        }
        status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be marked as delivered.");
        }
        status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Only placed orders can be canceled.");
        }
        status = OrderStatus.CANCELLED;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void updateTotalNumberOfItems() {
        totalNumberOfItems = items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getTotalNumberOfItems() {
        return totalNumberOfItems;
    }
}
