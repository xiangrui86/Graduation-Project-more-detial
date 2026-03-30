package com.mall.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 500)
    private String description;

    /** 详细描述/商品详情 */
    @Column(name = "detail_description", columnDefinition = "LONGTEXT")
    private String detailDescription;

    @Column(length = 512)
    private String image;

    /** 商品主图多图，逗号分隔URL */
    @Column(name = "image_list", columnDefinition = "TEXT")
    private String imageList;

    /** 商品详情页轮播图，多个用逗号分隔 */
    @Column(name = "detail_images", columnDefinition = "TEXT")
    private String detailImages;

    /** 品牌 */
    @Column(length = 64)
    private String brand;

    /** 商品参数/规格说明 */
    @Column(name = "attributes", columnDefinition = "LONGTEXT")
    private String attributes;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    /** 价格单位 */
    @Builder.Default
    @Column(length = 32)
    private String unit = "件";

    @Builder.Default
    @Column(nullable = false)
    private Integer stock = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer sales = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean onSale = true;

    @Builder.Default
    @Column(name = "review_status", length = 20)
    private String reviewStatus = "PENDING";

    @Column(name = "review_reason", length = 512)
    private String reviewReason;

    @Builder.Default
    @Column(name = "is_new")
    private Boolean isNew = false;

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
