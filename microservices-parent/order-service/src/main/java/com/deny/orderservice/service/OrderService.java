package com.deny.orderservice.service;

import com.deny.orderservice.dto.OrderLineItemDto;
import com.deny.orderservice.dto.OrderRequest;
import com.deny.orderservice.model.Order;
import com.deny.orderservice.model.OrderLineItem;
import com.deny.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        var order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        var orderLineItems = orderRequest.orderLineItems().stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItems(orderLineItems);
        orderRepository.save(order);
    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(orderLineItemDto.quantity());
        orderLineItem.setPrice(orderLineItemDto.price());
        orderLineItem.setSkuCode(orderLineItemDto.skuCode());
        return orderLineItem;
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
