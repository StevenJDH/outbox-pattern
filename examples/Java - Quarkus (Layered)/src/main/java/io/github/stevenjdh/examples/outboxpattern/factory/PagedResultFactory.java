/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.factory;

import io.github.stevenjdh.examples.outboxpattern.pagination.PagedResult;
import io.github.stevenjdh.examples.outboxpattern.repository.entity.OrderEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.List;

public class PagedResultFactory {

    private PagedResultFactory() {
    }
    
    public static PagedResult<OrderEntity> from(PanacheQuery<OrderEntity> query) {
        List<OrderEntity> orderEntities = query.list();
        
        return new PagedResult<>(
            orderEntities,
            query.page().index,
            query.page().size,
            query.count(),
            query.hasPreviousPage(),
            query.hasNextPage()
        );
    }
}
