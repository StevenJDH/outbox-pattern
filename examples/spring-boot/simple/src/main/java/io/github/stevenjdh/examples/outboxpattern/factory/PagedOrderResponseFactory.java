/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.factory;

import io.github.stevenjdh.examples.outboxpattern.models.dto.OrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.models.dto.PagedOrderResponseDTO;
import org.springframework.data.domain.Page;

public class PagedOrderResponseFactory {

    private PagedOrderResponseFactory() {
    }

    public static PagedOrderResponseDTO from(Page<OrderResponseDTO> page) {
        return new PagedOrderResponseDTO(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasPrevious(),
            page.hasNext()
        );
    }
}
