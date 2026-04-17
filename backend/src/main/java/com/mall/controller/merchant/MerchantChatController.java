package com.mall.controller.merchant;

import com.mall.dto.ChatMessageDTO;
import com.mall.dto.ChatMessageRequest;
import com.mall.dto.Result;
import com.mall.entity.ChatMessage;
import com.mall.entity.Product;
import com.mall.entity.User;
import com.mall.repository.ChatMessageRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/merchant/chat")
@RequiredArgsConstructor
public class MerchantChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getMerchantId() == null) {
            throw new RuntimeException("非商家账号");
        }
        return user.getMerchantId();
    }

    @GetMapping("/list")
    public Result<?> list(Authentication auth, @RequestParam(required = false) Long productId) {
        Long merchantId = currentMerchantId(auth);
        List<ChatMessage> messages;
        if (productId == null) {
            messages = chatMessageRepository.findByMerchantIdOrderByCreatedAtAsc(merchantId);
        } else {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isEmpty() || !productOpt.get().getMerchantId().equals(merchantId)) {
                return Result.fail("商品不存在或无权限查看消息");
            }
            messages = chatMessageRepository.findByProductIdOrderByCreatedAtAsc(productId);
        }
        return Result.ok(messages.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/send")
    public Result<?> send(Authentication auth, @RequestBody ChatMessageRequest request) {
        Long merchantId = currentMerchantId(auth);
        if (request.getProductId() == null) {
            return Result.fail("请指定商品");
        }
        if (request.getUserId() == null) {
            return Result.fail("请指定用户");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.fail("消息内容不能为空");
        }
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty() || !productOpt.get().getMerchantId().equals(merchantId)) {
            return Result.fail("商品不存在或无权限操作");
        }
        ChatMessage message = ChatMessage.builder()
                .userId(request.getUserId())
                .merchantId(merchantId)
                .productId(request.getProductId())
                .senderRole("MERCHANT")
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
