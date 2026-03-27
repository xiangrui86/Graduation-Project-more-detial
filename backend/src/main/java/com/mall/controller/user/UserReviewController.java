package com.mall.controller.user;

import com.mall.dto.Result;
import com.mall.entity.OrderItem;
import com.mall.entity.ProductReview;
import com.mall.repository.OrderItemRepository;
import com.mall.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/review")
@RequiredArgsConstructor
/** 用户评价接口：提交商品评价。 */
public class UserReviewController {

    private final ProductReviewRepository productReviewRepository;
    private final OrderItemRepository orderItemRepository;

    /** 获取当前登录用户 ID。 */
    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    /** 新增商品评价。 */
    @PostMapping
    public ResponseEntity<Result<?>> add(Authentication auth, @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Long orderId = body.containsKey("orderId") ? Long.valueOf(body.get("orderId").toString()) : null;
        int rating = body.containsKey("rating") ? ((Number) body.get("rating")).intValue() : 5;
        String content = (String) body.get("content");
        Long userId = currentUserId(auth);

        // 检查该商品是否已评价过
        List<ProductReview> existingReviews = productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId, null).stream()
                .filter(r -> r.getUserId().equals(userId) && (orderId == null || r.getOrderId().equals(orderId)))
                .toList();
        
        if (!existingReviews.isEmpty()) {
            return ResponseEntity.ok(Result.fail("该商品已评价，不能重复评价"));
        }

        ProductReview r = ProductReview.builder()
                .productId(productId)
                .orderId(orderId)
                .userId(userId)
                .rating(rating)
                .content(content != null ? content : "")
                .build();
        productReviewRepository.save(r);

        // 标记该订单项为已评价
        if (orderId != null) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            for (OrderItem item : orderItems) {
                if (item.getProductId().equals(productId)) {
                    item.setReviewed(true);
                    orderItemRepository.save(item);
                    break;
                }
            }
        }

        return ResponseEntity.ok(Result.ok(r));
    }
}
