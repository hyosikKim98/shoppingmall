package project.shopping.domain.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private long unitPrice;

    public static OrderItem of(Long productId, int quantity, long unitPrice) {
        return new OrderItem(null, null, productId, quantity, unitPrice);
    }

    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
