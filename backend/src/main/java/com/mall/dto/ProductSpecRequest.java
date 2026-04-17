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
public class ProductSpecRequest {
    private String specName;
    private String specValue;
    private Integer stock;
    private BigDecimal priceDelta;
    private String image;
    private Integer sortOrder;
    private Boolean enabled;
}
