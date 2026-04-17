package com.mall.controller.user;

import com.mall.dto.ChatMessageDTO;
import com.mall.dto.ChatMessageRequest;
import com.mall.dto.Result;
import com.mall.entity.ChatMessage;
import com.mall.entity.Product;
import com.mall.repository.ChatMessageRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/chat")
@RequiredArgsConstructor
public class UserChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;

    @GetMapping("/list")
    public Result<?> list(Authentication auth, @RequestParam(required = false) Long productId) {
        Long userId = (Long) auth.getPrincipal();
        List<ChatMessage> messages;
        if (productId == null) {
            messages = chatMessageRepository.findThreadMessagesForUser(userId);
        } else {
            messages = chatMessageRepository.findByUserIdAndProductIdOrderByCreatedAtAsc(userId, productId);
        }
        return Result.ok(messages.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/send")
    public Result<?> send(Authentication auth, @RequestBody ChatMessageRequest request) {
        Long userId = (Long) auth.getPrincipal();
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.fail("消息内容不能为空");
        }
        Long merchantId = null;
        Long productId = request.getProductId();
        if (productId != null) {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty()) {
                return Result.fail("商品不存在");
            }
            merchantId = productOpt.get().getMerchantId();
        }
        ChatMessage message = ChatMessage.builder()
                .userId(userId)
                .merchantId(merchantId)
                .productId(productId)
                .senderRole("USER")
                .content(request.getContent().trim())
                .build();
        ChatMessage saved = chatMessageRepository.save(message);
        return Result.ok(toDto(saved));
    }

    private ChatMessageDTO toDto(ChatMessage msg) {
        String productName = null;
        if (msg.getProductId() != null) {
            productName = productRepository.findById(msg.getProductId())
                    .map(Product::getName)
                    .orElse(null);
        }
        return ChatMessageDTO.builder()
                .id(msg.getId())
                .userId(msg.getUserId())
                .merchantId(msg.getMerchantId())
                .productId(msg.getProductId())
                .productName(productName)
                .senderRole(msg.getSenderRole())
                .content(msg.getContent())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
