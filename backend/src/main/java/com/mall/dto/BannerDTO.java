package com.mall.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BannerDTO {
    private Long id;
    private String title;
    private Long productId;
    private String imageUrl;
    private String link;
    private Integer sortOrder;
    private Boolean enabled;

    // 商品展示信息
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String image;
    private Integer sales;
    private String categoryName;

    private String productName;
    private String productImage;
    private String productDescription;
    private BigDecimal productPrice;
    private BigDecimal productOriginalPrice;
    private String productCategoryName;
    private Integer productSales;
}
