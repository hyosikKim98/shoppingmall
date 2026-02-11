package project.shopping.domain.order.port.out;

public interface ProductStockRepository {

    /** 재고 차감(동시성 방어): 성공하면 true, 부족하면 false */
    boolean decreaseStockIfEnough(Long productId, int quantity);

    /** 주문 아이템 단가 산출용(현재 가격 조회) */
    long getCurrentPrice(Long productId);

    /** 주문 취소 시 재고 복구 */
    void increaseStock(Long productId, int quantity);
}
