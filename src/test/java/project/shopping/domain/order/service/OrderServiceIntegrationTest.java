package project.shopping.domain.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import project.shopping.TestLockRepositoryConfig;
import project.shopping.domain.order.dto.OrderCreateRequest;
import project.shopping.domain.order.dto.OrderItemRequest;
import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;
import project.shopping.domain.product.model.Product;
import project.shopping.infrastructure.persistence.mybatis.mapper.OrderMapper;
import project.shopping.infrastructure.persistence.mybatis.mapper.ProductMapper;
import project.shopping.infrastructure.persistence.mybatis.repository.MyBatisOrderRepository;
import project.shopping.infrastructure.persistence.mybatis.repository.MyBatisProductRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Import({OrderService.class, MyBatisOrderRepository.class, MyBatisProductRepository.class, TestLockRepositoryConfig.class})
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void createOrder_savesOrderAndItems_andDecreasesStock() {
        Product p1 = Product.createNew(1L, "Phone", 1000L, 5);
        Product p2 = Product.createNew(1L, "Laptop", 2000L, 2);
        productMapper.insert(p1);
        productMapper.insert(p2);

        OrderCreateRequest req = new OrderCreateRequest(List.of(
                new OrderItemRequest(p1.getId(), 2),
                new OrderItemRequest(p2.getId(), 1)
        ));

        var res = orderService.create(99L, req);

        Optional<Order> found = orderMapper.findById(res.id());
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo(99L);

        List<OrderItem> items = orderMapper.findItemsByOrderId(res.id());
        assertThat(items).hasSize(2);

        assertThat(productMapper.findById(p1.getId()).orElseThrow().getStock()).isEqualTo(3);
        assertThat(productMapper.findById(p2.getId()).orElseThrow().getStock()).isEqualTo(1);

        orderService.cancel(99L, res.id());

        assertThat(productMapper.findById(p1.getId()).orElseThrow().getStock()).isEqualTo(5);
        assertThat(productMapper.findById(p2.getId()).orElseThrow().getStock()).isEqualTo(2);
    }
}
