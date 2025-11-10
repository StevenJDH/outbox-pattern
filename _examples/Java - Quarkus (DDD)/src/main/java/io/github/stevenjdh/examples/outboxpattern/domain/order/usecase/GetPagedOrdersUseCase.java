/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.domain.order.usecase;

import io.github.stevenjdh.examples.outboxpattern.domain.order.aggregate.OrderAggregate;
import io.github.stevenjdh.examples.outboxpattern.shared.pagination.PagedResult;
import io.quarkus.panache.common.Page;

public interface GetPagedOrdersUseCase {
    
    PagedResult<OrderAggregate> getPagedOrders(Page page);
}
