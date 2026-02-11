package project.shopping.infrastructure.persistence.mybatis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.shopping.domain.order.port.out.ProductStockRepository;
import project.shopping.domain.product.dto.ProductSearchCondition;
import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.port.out.ProductRepository;
import project.shopping.infrastructure.persistence.mybatis.mapper.ProductMapper;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisProductRepository implements ProductRepository, ProductStockRepository {

    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        productMapper.insert(product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return productMapper.findById(productId);
    }

    @Override
    public List<Product> search(ProductSearchCondition cond) {
        int limit = cond.sizeOrDefault();
        int offset = cond.pageOrDefault() * limit;
        return productMapper.search(
                cond.keyword(),
                cond.minPrice(),
                cond.maxPrice(),
                normalizeSort(cond.sort()),
                limit,
                offset
        );
    }

    @Override
    public void update(Product product) {
        productMapper.update(product);
    }

    @Override
    public void deleteById(Long productId) {
        productMapper.deleteById(productId);
    }

    @Override
    public boolean isOwnedBySeller(Long productId, Long sellerId) {
        return productMapper.isOwnedBySeller(productId, sellerId);
    }

    @Override
    public boolean decreaseStockIfEnough(Long productId, int quantity) {
        return productMapper.decreaseStockIfEnough(productId, quantity) > 0;
    }

    @Override
    public long getCurrentPrice(Long productId) {
        Long price = productMapper.getCurrentPrice(productId);
        if (price == null) {
            throw new IllegalStateException("Product price not found: productId=" + productId);
        }
        return price;
    }

    @Override
    public void increaseStock(Long productId, int quantity) {
        productMapper.increaseStock(productId, quantity);
    }

    private String normalizeSort(String sort) {
        if (sort == null || sort.isBlank()) return null;
        String s = sort.toLowerCase(Locale.ROOT).trim();
        return switch (s) {
            case "createdat,asc" -> "createdAt,asc";
            case "createdat,desc" -> "createdAt,desc";
            case "price,asc" -> "price,asc";
            case "price,desc" -> "price,desc";
            default -> null;
        };
    }
}
