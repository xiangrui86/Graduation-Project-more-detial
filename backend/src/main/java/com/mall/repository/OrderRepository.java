package com.mall.repository;

import com.mall.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Order> findByMerchantIdOrderByCreatedAtDesc(Long merchantId, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Order> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = 'RECEIVED'")
    List<Order> findReceivedOrdersByUserId(Long userId);
}
