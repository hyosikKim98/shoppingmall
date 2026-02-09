package project.shopping.domain.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long customerId;
    private OrderStatus status;
    private long totalPrice;
    private OffsetDateTime createdAt;

    private final List<OrderItem> items = new ArrayList<>();

    public static Order createNew(Long customerId) {
        return new Order(null, customerId, OrderStatus.CREATED, 0L, OffsetDateTime.now());
    }

    public void addItem(OrderItem item) {
        items.add(item);
        totalPrice += (item.getUnitPrice() * item.getQuantity());
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELED) return;
        this.status = OrderStatus.CANCELED;
    }

    public void setId(Long id) { this.id = id; }
}
