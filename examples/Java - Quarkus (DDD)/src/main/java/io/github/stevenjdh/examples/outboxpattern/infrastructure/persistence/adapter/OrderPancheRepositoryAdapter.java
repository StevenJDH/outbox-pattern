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
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.entity.CustomerEntity;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.entity.OrderEntity;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.factory.PagedResultFactory;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.mapper.OrderItemPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.mapper.OrderPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.repository.OrderPancheRepository;
import io.github.stevenjdh.examples.outboxpattern.shared.pagination.PagedResult;
import io.micrometer.core.annotation.Timed;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class OrderPancheRepositoryAdapter implements OrderRepository  {

    private final OrderPancheRepository repository;
    private final OrderPersistenceMapper orderMapper;
    private final OrderItemPersistenceMapper itemMapper;
    private final EntityManager em;

    @Inject
    public OrderPancheRepositoryAdapter(OrderPancheRepository repository,
                                        OrderPersistenceMapper orderMapper,
                                        OrderItemPersistenceMapper itemMapper,
                                        EntityManager em) {
        
        this.repository = repository;
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.em = em;
    }

    @Override
    @Timed(value = "outboxpattern.order.repository", extraTags = {"contextual.name", "saveOrder"})
    public OrderAggregate save(OrderAggregate orderAggregate) {
        OrderEntity entity = orderMapper.toEntity(orderAggregate, itemMapper);
        // Avoids a full DB fetch, call is proxied (lazy loaded) like a transient entity,
        // which is not supported in Quarkus Panche like it is in Spring Boot JPA.
        var customerEntity = em.getReference(CustomerEntity.class, entity.getCustomer().getId());
        entity.setCustomer(customerEntity);
        repository.persist(entity);
        return orderMapper.toAggregate(entity);
    }

    @Override
    @Timed(value = "outboxpattern.order.repository", extraTags = {"contextual.name", "getPagedOrders"})
    public PagedResult<OrderAggregate> getOrders(Page page) {
        var pagedEntities = repository.findAll().page(page);
        return PagedResultFactory.from(pagedEntities)
                .map(orderMapper::toAggregate);
    }
}
