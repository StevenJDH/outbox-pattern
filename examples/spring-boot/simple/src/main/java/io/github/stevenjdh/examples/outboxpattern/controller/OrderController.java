package io.github.stevenjdh.examples.outboxpattern.controller;

import io.github.stevenjdh.examples.outboxpattern.factory.PagedOrderResponseFactory;
import io.github.stevenjdh.examples.outboxpattern.mapper.OrderDtoMapper;
import io.github.stevenjdh.examples.outboxpattern.dto.request.CreateOrderRequestDTO;
import io.github.stevenjdh.examples.outboxpattern.dto.response.OrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.dto.response.PagedOrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderDtoMapper mapper;

    public OrderController(OrderService orderService, OrderDtoMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequestDTO requestDTO) {
        var order = mapper.toOrder(requestDTO);
        var orderCreated = orderService.addOrder(order);
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
        
        var pagedOrders = orderService.getPagedOrders(pageLimited);
        var mappedResponse = pagedOrders.map(mapper::toOrderResponseDTO);
        var body = PagedOrderResponseFactory.from(mappedResponse);
        
        return ResponseEntity.ok(body);
    }
}
