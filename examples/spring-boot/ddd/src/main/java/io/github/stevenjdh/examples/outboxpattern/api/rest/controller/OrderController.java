/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.api.rest.controller;

import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.request.CreateOrderRequestDTO;
import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.OrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.api.rest.dto.response.PagedOrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.api.rest.factory.PagedOrderResponseFactory;
import io.github.stevenjdh.examples.outboxpattern.api.rest.mapper.OrderDtoMapper;
import io.github.stevenjdh.examples.outboxpattern.domain.order.usecase.PlaceOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import io.github.stevenjdh.examples.outboxpattern.domain.order.usecase.GetPagedOrdersUseCase;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetPagedOrdersUseCase getPagedOrdersUseCase;
    private final OrderDtoMapper mapper;

    public OrderController(PlaceOrderUseCase placeOrderUseCase,
                           GetPagedOrdersUseCase getPagedOrdersUseCase,
                           OrderDtoMapper mapper) {
        
        this.placeOrderUseCase = placeOrderUseCase;
        this.getPagedOrdersUseCase = getPagedOrdersUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequestDTO requestDTO) {
        var order = mapper.toAggregate(requestDTO);
        var orderCreated = placeOrderUseCase.addOrder(order);
        var body = mapper.toOrderResponseDTO(orderCreated);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(body);
    }
    
    @GetMapping
    public ResponseEntity<PagedOrderResponseDTO> getPagedOrders(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        
        int maxSize = 100; // Explicit limit instead of setting globally.
        int size = Math.min(pageable.getPageSize(), maxSize);
        var pageLimited = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        
        var pagedOrders = getPagedOrdersUseCase.getPagedOrders(pageLimited);
        var mappedResponse = pagedOrders.map(mapper::toOrderResponseDTO);
        var body = PagedOrderResponseFactory.from(mappedResponse);
        
        return ResponseEntity.ok(body);
    }
}
