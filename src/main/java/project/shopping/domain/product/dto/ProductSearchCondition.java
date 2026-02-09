package project.shopping.domain.product.dto;

public record ProductSearchCondition(
        String keyword,
        Long minPrice,
        Long maxPrice,
        String sort,   // "createdAt,desc" 같은 형태로 단순 처리
        Integer page,
        Integer size
) {
    public int pageOrDefault() { return page == null || page < 0 ? 0 : page; }
    public int sizeOrDefault() { return size == null || size <= 0 ? 20 : Math.min(size, 100); }
}