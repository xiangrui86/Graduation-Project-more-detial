package com.mall.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false, length = 32)
    private String orderNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    /** 订单状态：PENDING-待支付, PAID-已支付, SHIPPED-已发货, RECEIVED-已收货, CANCELLED-已取消 */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "pay_amount", precision = 12, scale = 2)
    private BigDecimal payAmount;

    @Column(name = "pay_method", length = 32)
    private String payMethod;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "receiver_name", length = 32)
    private String receiverName;

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;

    @Column(name = "receiver_address", length = 256)
    private String receiverAddress;

    /** 退货/退款状态说明：
     * REFUND_REQUESTED-已申请退货(退款)
     * REFUNDED-已退款
     */
    @Column(name = "refund_reason", length = 256)
    private String refundReason;

    @Column(name = "refund_request_time")
    private LocalDateTime refundRequestTime;

    @Column(name = "refund_reject_reason", length = 256)
    private String refundRejectReason;

    @Column(name = "refund_reject_time")
    private LocalDateTime refundRejectTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
