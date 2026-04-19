/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.api.rest.mapper;

import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.request.CreateOrderRequestDTO;
import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.OrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.domain.order.aggregate.OrderAggregate;
import java.time.Instant;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", imports = {UUID.class, Instant.class})
public interface OrderDtoMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "orderDate", expression = "java(Instant.now())")
    OrderAggregate toAggregate(CreateOrderRequestDTO requestDto);

    @Mapping(target = "orderId", source = "id")
    OrderResponseDTO toOrderResponseDTO(OrderAggregate aggregate);
}
