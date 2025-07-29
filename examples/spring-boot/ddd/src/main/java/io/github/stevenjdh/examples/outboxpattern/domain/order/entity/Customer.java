/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.domain.order.entity;

import io.github.stevenjdh.examples.outboxpattern.domain.shared.base.BaseEntity;
import java.util.UUID;

public class Customer extends BaseEntity<UUID> {

    private final String name;
    private final String email;
    private String shippingAddress;

    public Customer(UUID id, String name, String email, String shippingAddress) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.shippingAddress = shippingAddress;
    }

    public void updateShippingAddress(String newAddress) {
        this.shippingAddress = newAddress;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }
}
