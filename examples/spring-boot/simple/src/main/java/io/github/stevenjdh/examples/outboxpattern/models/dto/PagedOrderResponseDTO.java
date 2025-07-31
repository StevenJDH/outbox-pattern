/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.models.dto;

import java.util.List;

public record PagedOrderResponseDTO(
    List<OrderResponseDTO> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasPrevious,
    boolean hasNext
) {}
