package com.mall.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 支付记录：模拟支付时写入，保证数据完整性 */
@Entity
@Table(name = "payment_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_no", nullable = false, length = 32)
    private String orderNo;

    @Column(name = "pay_method", nullable = false, length = 32)
    private String payMethod;

    @Column(name = "pay_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal payAmount;

    @Column(name = "pay_time", nullable = false)
    private LocalDateTime payTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
