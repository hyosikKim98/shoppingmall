package project.shopping.domain.order.dto;

import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderStatus;

import java.time.OffsetDateTime;

public record OrderResponse(
        Long id,
        OrderStatus status,
        long totalPrice,
        OffsetDateTime createdAt
) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getStatus(), o.getTotalPrice(), o.getCreatedAt());
    }
}