package project.shopping.domain.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Setter
    private Long id;

    private Long sellerId;
    private String name;
    private long price;
    private int stock;
    private ProductStatus status;
    private OffsetDateTime createdAt;

    public static Product createNew(Long sellerId, String name, long price, int stock) {
        return new Product(null, sellerId, name, price, stock, ProductStatus.ACTIVE, OffsetDateTime.now());
    }

    public void update(String name, Long price, Integer stock, ProductStatus status) {
        if (name != null) this.name = name;
        if (price != null) this.price = price;
        if (stock != null) this.stock = stock;
        if (status != null) this.status = status;
    }

}