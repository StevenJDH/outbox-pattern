/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.api.rest.factory;

import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.OrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.PagedOrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.shared.pagination.PagedResult;

public class PagedOrderResponseFactory {

    private PagedOrderResponseFactory() {
    }

    public static PagedOrderResponseDTO from(PagedResult<OrderResponseDTO> pagedResult) {
        return new PagedOrderResponseDTO(
            pagedResult.content(),
            pagedResult.page(),
            pagedResult.size(),
            pagedResult.totalElements(),
            pagedResult.totalPages(),
            pagedResult.hasPrevious(),
            pagedResult.hasNext()
        );
    }
}
