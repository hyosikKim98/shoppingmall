package project.shopping.infrastructure.persistence.mybatis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;
import project.shopping.domain.order.port.out.OrderRepository;
import project.shopping.infrastructure.persistence.mybatis.mapper.OrderMapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisOrderRepository implements OrderRepository {

    private final OrderMapper orderMapper;

    @Override
    public Order saveOrder(Order order) {
        orderMapper.insertOrder(order);
        return order;
    }

    @Override
    public void saveItems(Long orderId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) return;
        orderMapper.insertItems(orderId, items);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        Optional<Order> found = orderMapper.findById(orderId);
        found.ifPresent(this::loadItems);
        return found;
    }

    @Override
    public Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId) {
        Optional<Order> found = orderMapper.findByIdAndCustomerId(orderId, customerId);
        found.ifPresent(this::loadItems);
        return found;
    }

    @Override
    public List<Order> findAllByCustomerId(Long customerId) {
        return orderMapper.findAllByCustomerId(customerId);
    }

    @Override
    public void updateStatus(Long orderId, String status) {
        orderMapper.updateStatus(orderId, status);
    }

    private void loadItems(Order order) {
        List<OrderItem> items = orderMapper.findItemsByOrderId(order.getId());
        order.getItems().clear();
        order.getItems().addAll(items);
    }
}
