package project.shopping.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.shopping.common.exception.BusinessException;
import project.shopping.common.exception.ErrorCode;
import project.shopping.domain.order.dto.OrderCreateRequest;
import project.shopping.domain.order.dto.OrderDetailResponse;
import project.shopping.domain.order.dto.OrderResponse;
import project.shopping.domain.order.model.Order;
import project.shopping.domain.order.model.OrderItem;
import project.shopping.domain.order.port.out.OrderRepository;
import project.shopping.domain.order.port.out.ProductStockRepository;
import project.shopping.infrastructure.persistence.redis.RedisLockException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductStockRepository productStockRepository;

    @Transactional
    public OrderResponse create(Long customerId, OrderCreateRequest req) {
        // 1) 재고 차감(조건부 update) + 단가 조회
        Order order = Order.createNew(customerId);

        for (var itemReq : req.items()) {
            boolean ok;
            try {
                ok = productStockRepository.decreaseStockIfEnough(itemReq.productId(), itemReq.quantity());
            } catch (RedisLockException e) {
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "Stock is busy, retry: productId=" + itemReq.productId());
            }
            if (!ok) throw new BusinessException(ErrorCode.OUT_OF_STOCK, "Out of stock: productId=" + itemReq.productId());

            long price = productStockRepository.getCurrentPrice(itemReq.productId());
            order.addItem(OrderItem.of(itemReq.productId(), itemReq.quantity(), price));
        }

        // 2) 주문 저장 + 아이템 저장
        Order saved = orderRepository.saveOrder(order);
        order.getItems().forEach(i -> i.setOrderId(saved.getId()));
        orderRepository.saveItems(saved.getId(), order.getItems());

        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(Long customerId) {
        return orderRepository.findAllByCustomerId(customerId).stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse myOrder(Long customerId, Long orderId) {
        Order o = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Order not found"));
        return OrderDetailResponse.from(o);
    }

    @Transactional
    public void cancel(Long customerId, Long orderId) {
        Order o = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Order not found"));

        o.cancel();
        orderRepository.updateStatus(orderId, o.getStatus().name());
        // (선택) 취소 시 재고 복구까지 하려면 ProductStockRepository.increaseStock(...) 추가
    }
}
