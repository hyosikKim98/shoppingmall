package project.shopping.infrastructure.persistence.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {

    int insertOrder(Order order);

    int insertItems(@Param("orderId") Long orderId, @Param("items") List<OrderItem> items);

    Optional<Order> findById(@Param("orderId") Long orderId);

    Optional<Order> findByIdAndCustomerId(@Param("orderId") Long orderId, @Param("customerId") Long customerId);

    List<Order> findAllByCustomerId(@Param("customerId") Long customerId);

    List<OrderItem> findItemsByOrderId(@Param("orderId") Long orderId);

    int updateStatus(@Param("orderId") Long orderId, @Param("status") String status);
}
