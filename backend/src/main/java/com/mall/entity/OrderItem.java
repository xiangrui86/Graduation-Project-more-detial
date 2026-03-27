package com.mall.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, length = 128)
    private String productName;

    @Column(length = 256)
    private String productImage;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "spec_id")
    private Long specId;

    /** 选中的规格值，记录快照 */
    @Column(name = "spec_value", length = 128)
    private String specValue;

    @Column(name = "sub_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal;

    /** 单品退款状态：null-无申请, REFUND_REQUESTED-申请中, REFUNDED-已退款 */
    @Column(name = "refund_status", length = 20)
    private String refundStatus;

    @Column(name = "refund_reason", length = 256)
    private String refundReason;

    @Column(name = "refund_request_time")
    private LocalDateTime refundRequestTime;

    /** 是否已评价 */
    @Column(name = "reviewed", nullable = false)
    @Builder.Default
    private Boolean reviewed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
