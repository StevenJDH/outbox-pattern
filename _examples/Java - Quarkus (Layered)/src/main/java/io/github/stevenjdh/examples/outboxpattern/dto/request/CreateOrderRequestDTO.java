/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025-2026 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateOrderRequestDTO(
    @Valid @NotNull CustomerDto customer,
    @Valid @NotNull @Size(min = 1, max = 10) List<OrderItemDto> items
) {
    public record CustomerDto(
        @NotBlank String id,
        String name,
        @Email String email,
        String shippingAddress
    ) {}

    public record OrderItemDto(
        @NotBlank String name,
        @Min(1) @Max(50) int quantity
    ) {}
}
