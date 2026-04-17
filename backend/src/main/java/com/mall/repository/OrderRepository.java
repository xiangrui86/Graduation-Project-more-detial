package com.mall.repository;

import com.mall.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Order> findByMerchantIdOrderByCreatedAtDesc(Long merchantId, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "(:orderNo IS NULL OR o.orderNo LIKE %:orderNo%) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:userId IS NULL OR o.userId = :userId) AND " +
            "(:merchantId IS NULL OR o.merchantId = :merchantId) " +
            "ORDER BY o.createdAt DESC")
    Page<Order> search(
            @Param("orderNo") String orderNo,
            @Param("status") String status,
            @Param("userId") Long userId,
            @Param("merchantId") Long merchantId,
            Pageable pageable);

    List<Order> findByUserIdAndStatus(Long userId, String status);

    List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime before);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = 'RECEIVED'")
    List<Order> findReceivedOrdersByUserId(Long userId);
}
