package com.mall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long userId;
    private Long merchantId;
    private Long productId;
    private String productName;
    private String senderRole;
    private String content;
    private LocalDateTime createdAt;
}
