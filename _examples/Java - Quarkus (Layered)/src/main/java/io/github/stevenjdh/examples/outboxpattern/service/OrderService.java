/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.service;

import io.github.stevenjdh.examples.outboxpattern.factory.OrderEventFactory;
import io.github.stevenjdh.examples.outboxpattern.factory.PagedResultFactory;
import io.github.stevenjdh.examples.outboxpattern.mapper.OrderItemPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.mapper.OrderPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.model.Order;
import io.github.stevenjdh.examples.outboxpattern.pagination.PagedResult;
import io.github.stevenjdh.examples.outboxpattern.repository.OrderPancheRepository;
import io.github.stevenjdh.examples.outboxpattern.repository.entity.CustomerEntity;
import io.github.stevenjdh.examples.outboxpattern.repository.entity.OrderEntity;
import io.micrometer.core.annotation.Timed;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrderService {

    private final OrderPancheRepository repository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final OrderPersistenceMapper orderMapper;
    private final OrderItemPersistenceMapper itemMapper;
    private final EntityManager em;
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class.getName()); 

    public OrderService(OrderPancheRepository repository,
                        OutboxEventPublisher outboxEventPublisher,
                        OrderPersistenceMapper orderMapper,
                        OrderItemPersistenceMapper itemMapper,
                        EntityManager em) {
        
        this.repository = repository;
        this.outboxEventPublisher = outboxEventPublisher;
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.em = em;
    }

    @WithSpan("add-order")
    @Transactional
    @Timed(value = "outboxpattern.order.repository", extraTags = {"contextual.name", "saveOrder"})
    public Order addOrder(Order order) {
        order.updateTotalNumberOfItems();
        LOG.info("Placing order [{}]...", order.getId());
        
        OrderEntity entity = orderMapper.toEntity(order, itemMapper);
        // Avoids a full DB fetch, call is proxied (lazy loaded) like a transient entity,
        // which is not supported in Quarkus Panche like it is in Spring Boot JPA.
        var customerEntity = em.getReference(CustomerEntity.class, entity.getCustomer().getId());
        entity.setCustomer(customerEntity);
        repository.persist(entity);
        var savedOrder = orderMapper.toOrder(entity);
        var event = OrderEventFactory.toOrderCreatedEvent(savedOrder);
        
        outboxEventPublisher.fire(event);
        LOG.info("Order [{}] has been placed for customer [{}].", savedOrder.getId(),
                savedOrder.getCustomer().getName());
        
        return savedOrder;
    }
    
    @WithSpan("get-paged-orders")
    @Timed(value = "outboxpattern.order.repository", extraTags = {"contextual.name", "getPagedOrders"})
    public PagedResult<Order> getPagedOrders(Page page) {
        LOG.info("Retrieving [{}] order(s) from page [{}]...", page.size,
                page.index);
        
        var pagedOrders = repository.findAll().page(page);

        LOG.info("Found [{}] order(s) on page [{}].", pagedOrders.list().size(),
                page.index);
        
        return PagedResultFactory.from(pagedOrders)
                .map(orderMapper::toOrder);
    }
}
