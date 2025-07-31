/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.adapter;

import io.github.stevenjdh.examples.outboxpattern.domain.order.aggregate.OrderAggregate;
import io.github.stevenjdh.examples.outboxpattern.domain.order.repository.OrderRepository;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.mapper.OrderItemPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.mapper.OrderPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.repository.OrderJpaRepository;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrderJpaRepositoryAdapter implements OrderRepository  {

    private final OrderJpaRepository repository;
    private final OrderPersistenceMapper orderMapper;
    private final OrderItemPersistenceMapper itemMapper;

    public OrderJpaRepositoryAdapter(OrderJpaRepository repository,
                                     OrderPersistenceMapper orderMapper,
                                     OrderItemPersistenceMapper itemMapper) {
        
        this.repository = repository;
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    @Observed(name = "outboxpattern.order.repository", contextualName = "saveOrder")
    public OrderAggregate save(OrderAggregate orderAggregate) {
        var entity = orderMapper.toEntity(orderAggregate, itemMapper);
        var savedEntity = repository.save(entity);
        return orderMapper.toAggregate(savedEntity);
    }

    @Override
    @Observed(name = "outboxpattern.order.repository", contextualName = "getPagedOrders")
    public Page<OrderAggregate> getOrders(Pageable pageable) {
        var pagedEntities = repository.findAll(pageable);
        return pagedEntities.map(orderMapper::toAggregate);
    }
}
