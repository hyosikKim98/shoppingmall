package project.shopping.domain.product.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.shopping.common.response.ApiResponse;
import project.shopping.common.util.TraceIdUtil;
import project.shopping.domain.product.dto.ProductResponse;
import project.shopping.domain.product.dto.ProductSearchCondition;
import project.shopping.domain.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) { this.productService = productService; }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(ProductSearchCondition cond) {
        List<ProductResponse> list = productService.search(cond);
        return ResponseEntity.ok(ApiResponse.ok(list, TraceIdUtil.ensureTraceId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable("id") Long id) {
        ProductResponse res = productService.get(id);
        return ResponseEntity.ok(ApiResponse.ok(res, TraceIdUtil.ensureTraceId()));
    }
}