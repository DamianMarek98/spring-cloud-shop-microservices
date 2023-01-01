package com.deny.orderservice.dto;

import java.util.List;

public record OrderRequest(List<OrderLineItemDto> orderLineItems) {
}
