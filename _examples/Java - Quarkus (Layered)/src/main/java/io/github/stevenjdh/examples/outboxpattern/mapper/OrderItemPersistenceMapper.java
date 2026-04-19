/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.mapper;

import io.github.stevenjdh.examples.outboxpattern.model.OrderItem;
import io.github.stevenjdh.examples.outboxpattern.repository.entity.OrderEntity;
import io.github.stevenjdh.examples.outboxpattern.repository.entity.OrderItemEntity;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi", imports = UUID.class, uses = {OrderPersistenceMapper.class})
public interface OrderItemPersistenceMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "order", ignore = true)
    OrderItemEntity toEntity(OrderItem item, @Context OrderEntity orderEntity);

    @AfterMapping
    default void afterToEntity(@MappingTarget OrderItemEntity entity, OrderItem item,
            @Context OrderEntity orderEntity) {

        entity.setOrder(orderEntity);
    }

    OrderItem toValueObject(OrderItemEntity entity);
}
