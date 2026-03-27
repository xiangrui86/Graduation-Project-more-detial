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
    private String detailDescription;
    private String detailImages;
    private java.util.List<String> images;  // 前端传来的图片数组，会转换为 detailImages
    private String brand;
    private String attributes;
    private String unit;  // 价格单位
    private Long categoryId;
    private String categoryName;  // 自定义分类名称（可选）
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String image;
    private Boolean isNew;
    private Boolean onSale;
}
