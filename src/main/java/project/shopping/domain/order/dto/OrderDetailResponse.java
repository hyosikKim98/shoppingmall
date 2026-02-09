package project.shopping.domain.order.dto;

import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;
import project.shopping.domain.order.model.OrderStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        OrderStatus status,
        long totalPrice,
        OffsetDateTime createdAt,
        List<OrderLine> items
) {
    public record OrderLine(Long productId, int quantity, long unitPrice) {
        public static OrderLine from(OrderItem i) {
            return new OrderLine(i.getProductId(), i.getQuantity(), i.getUnitPrice());
        }
    }

    public static OrderDetailResponse from(Order o) {
        return new OrderDetailResponse(
                o.getId(),
                o.getStatus(),
                o.getTotalPrice(),
                o.getCreatedAt(),
                o.getItems().stream().map(OrderLine::from).toList()
        );
    }
}
