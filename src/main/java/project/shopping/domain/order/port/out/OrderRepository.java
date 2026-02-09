package project.shopping.domain.order.port.out;

import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order saveOrder(Order order);
    void saveItems(Long orderId, List<OrderItem> items);

    Optional<Order> findById(Long orderId);
    Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId);
    List<Order> findAllByCustomerId(Long customerId);

    void updateStatus(Long orderId, String status);
}
