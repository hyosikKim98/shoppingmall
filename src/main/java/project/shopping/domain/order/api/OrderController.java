package project.shopping.domain.order.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.shopping.common.response.ApiResponse;
import project.shopping.common.security.CustomUserDetails;
import project.shopping.common.util.TraceIdUtil;
import project.shopping.domain.order.dto.OrderCreateRequest;
import project.shopping.domain.order.dto.OrderDetailResponse;
import project.shopping.domain.order.dto.OrderResponse;
import project.shopping.domain.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody OrderCreateRequest req
    ) {
        OrderResponse res = orderService.create(user.getUserId(), req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(res, TraceIdUtil.ensureTraceId()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> myOrders(@AuthenticationPrincipal CustomUserDetails user) {
        List<OrderResponse> list = orderService.myOrders(user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok(list, TraceIdUtil.ensureTraceId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> myOrder(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("id") Long id
    ) {
        OrderDetailResponse res = orderService.myOrder(user.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.ok(res, TraceIdUtil.ensureTraceId()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("id") Long id
    ) {
        orderService.cancel(user.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.ok(TraceIdUtil.ensureTraceId()));
    }
}