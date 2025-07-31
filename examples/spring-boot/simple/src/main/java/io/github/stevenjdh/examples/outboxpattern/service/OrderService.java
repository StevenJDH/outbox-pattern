/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.service;

import io.github.stevenjdh.examples.outboxpattern.factory.OrderEventFactory;
import io.github.stevenjdh.examples.outboxpattern.mapper.OrderItemPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.mapper.OrderPersistenceMapper;
import io.github.stevenjdh.examples.outboxpattern.models.Order;
import io.github.stevenjdh.examples.outboxpattern.repository.OrderJpaRepository;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderJpaRepository repository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final OrderPersistenceMapper orderMapper;
    private final OrderItemPersistenceMapper itemMapper;
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class.getName()); 

    public OrderService(OrderJpaRepository repository,
                        OutboxEventPublisher outboxEventPublisher,
                        OrderPersistenceMapper orderMapper,
                        OrderItemPersistenceMapper itemMapper) {
        
        this.repository = repository;
        this.outboxEventPublisher = outboxEventPublisher;
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
    }

    @Transactional
    @Observed(name = "outboxpattern.order.repository", contextualName = "saveOrder")
    public Order addOrder(Order order) {
        order.updateTotalNumberOfItems();
        LOG.info("Placing order [{}]...", order.getId());
        
        var entity = orderMapper.toEntity(order, itemMapper);
        var savedEntity = repository.save(entity);
        var savedOrder = orderMapper.toOrder(savedEntity);
        var event = OrderEventFactory.toOrderCreatedEvent(savedOrder);
        
        outboxEventPublisher.fire(event);
        LOG.info("Order [{}] has been placed for customer [{}].", savedEntity.getId(),
                savedEntity.getCustomer().getName());
        
        return savedOrder;
    }

    @Observed(name = "outboxpattern.order.repository", contextualName = "getPagedOrders")
    public Page<Order> getPagedOrders(Pageable pageable) {
        LOG.info("Retrieving the first [{}] orders from page [{}]...", pageable.getPageSize(),
                pageable.getPageNumber());
        
        var pagedOrders = repository.findAll(pageable);
        
        LOG.info("Found [{}] order(s) on page [{}].", pagedOrders.getNumberOfElements(),
                pageable.getPageNumber());
        
        return pagedOrders.map(orderMapper::toOrder);
    }
}