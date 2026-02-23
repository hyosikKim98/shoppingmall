package project.shopping.infrastructure.persistence.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.shopping.domain.product.model.Product;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {

    int insert(Product product);

    Optional<Product> findById(@Param("id") Long id);

    List<Product> search(@Param("keyword") String keyword,
                         @Param("minPrice") Long minPrice,
                         @Param("maxPrice") Long maxPrice,
                         @Param("sort") String sort,
                         @Param("limit") int limit,
                         @Param("offset") int offset);

    int update(Product product);

    int deleteById(@Param("id") Long id);

    boolean isOwnedBySeller(@Param("productId") Long productId, @Param("sellerId") Long sellerId);

    int decreaseStockIfEnough(@Param("productId") Long productId, @Param("quantity") int quantity);

    Long getCurrentPrice(@Param("productId") Long productId);

    int increaseStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    long sumActiveStock();

    int countActiveLowStock(@Param("threshold") int threshold);

    int countActiveOutOfStock();
}
