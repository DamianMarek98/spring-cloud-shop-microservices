package com.deny.orderservice.dto;

import java.util.List;

public record OrderDto(List<OrderLineItemDto> orderLineItems) {
}
