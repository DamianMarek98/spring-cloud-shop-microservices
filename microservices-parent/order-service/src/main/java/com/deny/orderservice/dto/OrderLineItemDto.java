package com.deny.orderservice.dto;

import java.math.BigDecimal;

public record OrderLineItemDto(String skuCode, BigDecimal price, Integer quantity) {
}
