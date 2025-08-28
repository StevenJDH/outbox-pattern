/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.config;

import io.github.stevenjdh.examples.outboxpattern.dto.response.ErrorResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.model.Customer;
import io.github.stevenjdh.examples.outboxpattern.model.Order;
import io.github.stevenjdh.examples.outboxpattern.model.OrderItem;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(
    targets = {
        Order.class,
        Customer.class,
        OrderItem.class,
        ErrorResponseDTO.class
    },
    fields = true,
    methods = true
)
public class ReflectionConfig {

}
