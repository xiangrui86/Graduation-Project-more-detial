package com.mall.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 轮播图标题 */
    @Column(length = 128)
    private String title;

    /** 关联商品ID（已上架商品） */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 轮播图图片URL */
    @Column(length = 512, nullable = false)
    private String imageUrl;

    /** 跳转链接（可选，默认跳商品详情） */
    @Column(length = 512)
    private String link;

    /** 排序权重 */
    @Column(nullable = false)
    private Integer sortOrder = 0;

    /** 是否启用 */
    @Column(nullable = false)
    private Boolean enabled = true;

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
