package com.mall.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequest {

    private Long productId;

    private Long specId;

    private Integer quantity;
}
