/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.application.order.usecase;

import io.github.stevenjdh.examples.outboxpattern.domain.order.aggregate.OrderAggregate;
import io.github.stevenjdh.examples.outboxpattern.domain.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.stevenjdh.examples.outboxpattern.domain.order.usecase.GetPagedOrdersUseCase;
import io.github.stevenjdh.examples.outboxpattern.shared.pagination.PagedResult;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetPagedOrdersUseCaseImpl implements GetPagedOrdersUseCase {

    private final OrderRepository orderRepository;
    private static final Logger LOG = LoggerFactory.getLogger(GetPagedOrdersUseCaseImpl.class.getName()); 
    
    @Inject
    public GetPagedOrdersUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PagedResult<OrderAggregate> getPagedOrders(Page page) {
        LOG.info("Retrieving [{}] order(s) from page [{}]...", page.size,
                page.index);
        
        var pagedOrders = orderRepository.getOrders(page);

        LOG.info("Found [{}] order(s) on page [{}].", pagedOrders.content().size(),
                page.index);
        
        return pagedOrders;
    }
}
