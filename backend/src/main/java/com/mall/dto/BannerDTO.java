package com.mall.dto;

import lombok.Data;

@Data
public class BannerDTO {
    private Long id;
    private String title;
    private Long productId;
    private String imageUrl;
    private String link;
    private Integer sortOrder;
    private Boolean enabled;
    private String productName; // 展示用
    private String productImage;
}
