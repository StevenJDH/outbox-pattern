/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.application.order.usecase;

import io.github.stevenjdh.examples.outboxpattern.domain.order.event.OrderEventFactory;
import io.github.stevenjdh.examples.outboxpattern.domain.order.aggregate.OrderAggregate;
import io.github.stevenjdh.examples.outboxpattern.domain.order.repository.OrderRepository;
import io.github.stevenjdh.examples.outboxpattern.domain.order.usecase.PlaceOrderUseCase;
import io.github.stevenjdh.examples.outboxpattern.domain.shared.event.OutboxEventPublisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlaceOrderUseCaseImpl implements PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final OutboxEventPublisher outboxEventPublisher;
    private static final Logger LOG = LoggerFactory.getLogger(PlaceOrderUseCaseImpl.class.getName()); 

    public PlaceOrderUseCaseImpl(OrderRepository orderRepository, OutboxEventPublisher outboxEventPublisher) {
        this.orderRepository = orderRepository;
        this.outboxEventPublisher = outboxEventPublisher;
    }

    @Override
    @Transactional
    public OrderAggregate addOrder(OrderAggregate order) {
        order.updateTotalNumberOfItems();
        LOG.info("Placing order [{}]...", order.getId());
        
        var savedOrder = orderRepository.save(order);
        var event = OrderEventFactory.toOrderCreatedEvent(savedOrder);
        
        outboxEventPublisher.fire(event);
        LOG.info("Order [{}] has been placed for customer [{}].", savedOrder.getId(),
                savedOrder.getCustomer().getName());
        
        return savedOrder;
    }
}
