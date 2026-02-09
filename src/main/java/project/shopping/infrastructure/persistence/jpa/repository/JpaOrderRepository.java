package project.shopping.infrastructure.persistence.jpa.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;
import project.shopping.domain.order.port.out.OrderRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class JpaOrderRepository implements OrderRepository {
    @Override
    public Order saveOrder(Order order) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public void saveItems(Long orderId, List<OrderItem> items) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public List<Order> findAllByCustomerId(Long customerId) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }

    @Override
    public void updateStatus(Long orderId, String status) {
        throw new UnsupportedOperationException("JPA repository not implemented");
    }
}
