package project.shopping.domain.product.dto;

import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.model.ProductStatus;

import java.time.OffsetDateTime;

public record ProductResponse(
        Long id,
        Long sellerId,
        String name,
        long price,
        int stock,
        ProductStatus status,
        OffsetDateTime createdAt
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getSellerId(), p.getName(), p.getPrice(), p.getStock(), p.getStatus(), p.getCreatedAt());
    }
}
