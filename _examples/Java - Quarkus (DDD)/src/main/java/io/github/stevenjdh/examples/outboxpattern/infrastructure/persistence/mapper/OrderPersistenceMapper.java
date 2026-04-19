/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.mapper;

import io.github.stevenjdh.examples.outboxpattern.domain.order.aggregate.OrderAggregate;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.entity.OrderEntity;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.entity.OrderItemEntity;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface OrderPersistenceMapper {

    @Mapping(target = "items", ignore = true)
    OrderEntity toEntity(OrderAggregate order, @Context OrderItemPersistenceMapper itemMapper);

    @AfterMapping
    default void afterToEntity(@MappingTarget OrderEntity entity, OrderAggregate order,
            @Context OrderItemPersistenceMapper itemMapper) {

        if (order.getItems() != null) {
            List<OrderItemEntity> items = order.getItems().stream()
                    .map(item -> itemMapper.toEntity(item, entity))
                    .toList();
            entity.setItems(items);
        }
    }

    OrderAggregate toAggregate(OrderEntity entity);

    @AfterMapping
    default void afterToAggregate(@MappingTarget OrderAggregate order, OrderEntity entity) {
        order.updateTotalNumberOfItems();
    }
}
