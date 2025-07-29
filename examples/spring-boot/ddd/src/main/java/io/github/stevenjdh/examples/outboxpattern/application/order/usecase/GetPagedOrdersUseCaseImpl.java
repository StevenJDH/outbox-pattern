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
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.github.stevenjdh.examples.outboxpattern.domain.order.usecase.GetPagedOrdersUseCase;

@Service
public class GetPagedOrdersUseCaseImpl implements GetPagedOrdersUseCase {

    private final OrderRepository orderRepository;
    private static final Logger LOG = LoggerFactory.getLogger(GetPagedOrdersUseCaseImpl.class.getName()); 
    
    public GetPagedOrdersUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<OrderAggregate> getPagedOrders(Pageable pageable) {
        LOG.info("Retrieving the first [{}] orders from page [{}]...", pageable.getPageSize(),
                pageable.getPageNumber());
        
        var pagedOrders = orderRepository.getOrders(pageable);
        
        LOG.info("Found [{}] order(s) on page [{}].", pagedOrders.getNumberOfElements(),
                pageable.getPageNumber());
        
        return pagedOrders;
    }
}
