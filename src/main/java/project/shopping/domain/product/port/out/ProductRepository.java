package project.shopping.domain.product.port.out;

import project.shopping.domain.product.dto.ProductSearchCondition;
import project.shopping.domain.product.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long productId);
    List<Product> search(ProductSearchCondition cond);
    void update(Product product);
    void deleteById(Long productId);

    /** 판매자 소유 확인용 */
    boolean isOwnedBySeller(Long productId, Long sellerId);
}