package project.shopping.infrastructure.persistence.mybatis;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;
import project.shopping.domain.order.model.OrderStatus;
import project.shopping.infrastructure.persistence.mybatis.mapper.OrderMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void insertFindAndUpdate() {
        Order order = Order.createNew(99L);
        order.addItem(OrderItem.of(1L, 2, 1000L));
        order.addItem(OrderItem.of(2L, 1, 500L));

        int inserted = orderMapper.insertOrder(order);
        assertThat(inserted).isEqualTo(1);
        assertThat(order.getId()).isNotNull();

        order.getItems().forEach(i -> i.setOrderId(order.getId()));
        int itemsInserted = orderMapper.insertItems(order.getId(), order.getItems());
        assertThat(itemsInserted).isEqualTo(2);

        Optional<Order> found = orderMapper.findById(order.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo(99L);

        Optional<Order> foundByCustomer = orderMapper.findByIdAndCustomerId(order.getId(), 99L);
        assertThat(foundByCustomer).isPresent();

        List<Order> all = orderMapper.findAllByCustomerId(99L);
        assertThat(all).hasSize(1);

        List<OrderItem> items = orderMapper.findItemsByOrderId(order.getId());
        assertThat(items).hasSize(2);

        int updated = orderMapper.updateStatus(order.getId(), OrderStatus.CANCELED.name());
        assertThat(updated).isEqualTo(1);
    }
}
