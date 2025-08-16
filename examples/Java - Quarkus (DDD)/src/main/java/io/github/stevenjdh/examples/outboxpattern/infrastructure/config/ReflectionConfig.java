/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.config;

import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.ErrorResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.domain.order.aggregate.OrderAggregate;
import io.github.stevenjdh.examples.outboxpattern.domain.order.entity.Customer;
import io.github.stevenjdh.examples.outboxpattern.domain.order.value.OrderItem;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(
    targets = {
        OrderAggregate.class,
        Customer.class,
        OrderItem.class,
        ErrorResponseDTO.class
    },
    fields = true,
    methods = true
)
public class ReflectionConfig {

}
