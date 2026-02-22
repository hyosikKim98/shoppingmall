package project.shopping.domain.product.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.shopping.common.response.ApiResponse;
import project.shopping.common.security.CustomUserDetails;
import project.shopping.common.util.TraceIdUtil;
import project.shopping.domain.product.dto.ProductCreateRequest;
import project.shopping.domain.product.dto.ProductResponse;
import project.shopping.domain.product.dto.ProductUpdateRequest;
import project.shopping.domain.product.service.ProductService;

@RestController
@RequestMapping("/api/seller/products")
public class SellerProductController {

    private final ProductService productService;

    public SellerProductController(ProductService productService) { this.productService = productService; }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ProductCreateRequest req
    ) {
        ProductResponse res = productService.create(user.getUserId(), req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(res, TraceIdUtil.ensureTraceId()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductUpdateRequest req
    ) {
        ProductResponse res = productService.update(user.getUserId(), id, req);
        return ResponseEntity.ok(ApiResponse.ok(res, TraceIdUtil.ensureTraceId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("id") Long id
    ) {
        productService.delete(user.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.ok(TraceIdUtil.ensureTraceId()));
    }
}