package com.mall.repository;

import com.mall.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi.productId FROM OrderItem oi JOIN Order o ON oi.orderId = o.id WHERE o.userId = :userId AND o.status = 'RECEIVED'")
    List<Long> findReceivedProductIdsByUserId(Long userId);

    /** 协同过滤：所有已收货订单的 (用户ID, 商品ID)，排除指定用户 */
    @Query(value = "SELECT o.user_id, oi.product_id FROM orders o JOIN order_item oi ON oi.order_id = o.id WHERE o.status = 'RECEIVED' AND o.user_id <> :excludeUserId", nativeQuery = true)
    List<Object[]> findOtherUsersReceivedProductsRaw(Long excludeUserId);
}
