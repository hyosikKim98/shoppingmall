package project.shopping.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.shopping.common.exception.BusinessException;
import project.shopping.common.exception.ErrorCode;
import project.shopping.domain.product.dto.ProductCreateRequest;
import project.shopping.domain.product.dto.ProductResponse;
import project.shopping.domain.product.dto.ProductSearchCondition;
import project.shopping.domain.product.dto.ProductUpdateRequest;
import project.shopping.domain.product.model.Product;
import project.shopping.domain.product.model.ProductStatus;
import project.shopping.domain.product.port.out.ProductRepository;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(Long sellerId, ProductCreateRequest req) {
        Product saved = productRepository.save(Product.createNew(sellerId, req.name(), req.price(), req.stock()));
        return ProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Product not found"));
        return ProductResponse.from(p);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> search(ProductSearchCondition cond) {
        return productRepository.search(cond).stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse update(Long sellerId, Long productId, ProductUpdateRequest req) {
        if (!productRepository.isOwnedBySeller(productId, sellerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Not your product");
        }

        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Product not found"));

        ProductStatus status = null;
        if (req.status() != null) {
            status = ProductStatus.valueOf(req.status().toUpperCase(Locale.ROOT));
        }

        p.update(req.name(), req.price(), req.stock(), status);
        productRepository.update(p);

        return ProductResponse.from(p);
    }

    @Transactional
    public void delete(Long sellerId, Long productId) {
        if (!productRepository.isOwnedBySeller(productId, sellerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Not your product");
        }
        productRepository.deleteById(productId);
    }
}