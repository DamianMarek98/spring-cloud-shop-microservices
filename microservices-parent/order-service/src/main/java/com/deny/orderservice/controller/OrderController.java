package com.deny.orderservice.controller;

import com.deny.orderservice.dto.OrderDto;
import com.deny.orderservice.dto.OrderLineItemDto;
import com.deny.orderservice.dto.OrderRequest;
import com.deny.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order placed successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getOrders() {
        return orderService.getAllOrders().stream()
                .map(order -> {
                    var orderLinesDto = order.getOrderLineItems().stream()
                            .map(orderLineItem -> new OrderLineItemDto(orderLineItem.getSkuCode(),
                                    orderLineItem.getPrice(), orderLineItem.getQuantity()))
                            .toList();
                    return new OrderDto(orderLinesDto);
                }).toList();
    }
}
