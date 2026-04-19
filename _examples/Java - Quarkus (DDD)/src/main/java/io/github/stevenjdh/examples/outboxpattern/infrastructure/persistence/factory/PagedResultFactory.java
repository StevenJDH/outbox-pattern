/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.factory;

import io.github.stevenjdh.examples.outboxpattern.infrastructure.persistence.entity.OrderEntity;
import io.github.stevenjdh.examples.outboxpattern.shared.pagination.PagedResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

public class PagedResultFactory {

    private PagedResultFactory() {
    }
    
    public static PagedResult<OrderEntity> from(PanacheQuery<OrderEntity> query) {
        return new PagedResult<>(
            query.list(),
            query.page().index,
            query.page().size,
            query.count(),
            query.hasPreviousPage(),
            query.hasNextPage()
        );
    }
}
