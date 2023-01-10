package com.deny.orderservice.controller;

import com.deny.orderservice.dto.OrderDto;
import com.deny.orderservice.dto.OrderLineItemDto;
import com.deny.orderservice.dto.OrderRequest;
import com.deny.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something is wrong, order after some time please!");
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
