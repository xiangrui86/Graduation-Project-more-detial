package com.mall.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {

    private Long id;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal price;

    private Long specId;

    private String specValue;

    private String specName;

    private Integer quantity;

    private BigDecimal totalPrice;
}
