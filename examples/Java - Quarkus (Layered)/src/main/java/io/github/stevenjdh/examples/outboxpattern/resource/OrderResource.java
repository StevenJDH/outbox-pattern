/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.resource;

import io.github.stevenjdh.examples.outboxpattern.dto.request.CreateOrderRequestDTO;
import io.github.stevenjdh.examples.outboxpattern.dto.response.OrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.dto.response.PagedOrderResponseDTO;
import io.github.stevenjdh.examples.outboxpattern.factory.PagedOrderResponseFactory;
import io.github.stevenjdh.examples.outboxpattern.mapper.OrderDtoMapper;
import io.github.stevenjdh.examples.outboxpattern.service.OrderService;
import io.quarkus.panache.common.Page;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response.Status;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/v1/orders")
public class OrderResource{

    private final OrderService orderService;
    private final OrderDtoMapper mapper;

    @Inject
    public OrderResource(OrderService orderService, OrderDtoMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @POST
    public RestResponse<OrderResponseDTO> createOrder(@Valid CreateOrderRequestDTO requestDTO) {
        var order = mapper.toOrder(requestDTO);
        var orderCreated = orderService.addOrder(order);
        var body = mapper.toOrderResponseDTO(orderCreated);
        
        return RestResponse.status(Status.CREATED, body);
    }
    
    @GET
    public RestResponse<PagedOrderResponseDTO> getPagedOrders(@QueryParam("page") @DefaultValue("0") int pageIndex,
                                                              @QueryParam("size") @DefaultValue("10") int pageSize) {
        
        int maxSize = 100; // Explicit limit instead of setting globally.
        int size = Math.min(pageSize, maxSize);
        var pageLimited = Page.of(pageIndex, size);
        
        var pagedOrders = orderService.getPagedOrders(pageLimited);
        var mappedResponse = pagedOrders.map(mapper::toOrderResponseDTO);
        var body = PagedOrderResponseFactory.from(mappedResponse);
        
        return RestResponse.ok(body);
    }
}
