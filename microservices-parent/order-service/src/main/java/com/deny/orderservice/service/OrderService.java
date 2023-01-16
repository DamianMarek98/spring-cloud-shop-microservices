package com.deny.orderservice.service;

import com.deny.orderservice.dto.InventoryResponse;
import com.deny.orderservice.dto.OrderLineItemDto;
import com.deny.orderservice.dto.OrderRequest;
import com.deny.orderservice.event.OrderPlacedEvent;
import com.deny.orderservice.model.Order;
import com.deny.orderservice.model.OrderLineItem;
import com.deny.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        var order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        var orderLineItems = orderRequest.orderLineItems().stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItems(orderLineItems);

        var skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();
        var inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if (inventoryResponses == null) {
            throw new IllegalStateException("Inventory service not available!");
        }
        final boolean allProductsAreInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        if (allProductsAreInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully";
        }

        throw new IllegalStateException("Product is not in stock!");
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
