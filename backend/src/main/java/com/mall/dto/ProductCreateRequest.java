package com.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;  // 自定义分类名称（可选）
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String image;
    private Boolean isNew;
    private Boolean onSale;
}
