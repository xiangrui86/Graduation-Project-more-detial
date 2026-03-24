package com.mall.controller.user;

import com.mall.dto.Result;
import com.mall.entity.ProductReview;
import com.mall.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/review")
@RequiredArgsConstructor
/** 用户评价接口：提交商品评价。 */
public class UserReviewController {

    private final ProductReviewRepository productReviewRepository;

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
        ProductReview r = ProductReview.builder()
                .productId(productId)
                .orderId(orderId)
                .userId(currentUserId(auth))
                .rating(rating)
                .content(content != null ? content : "")
                .build();
        productReviewRepository.save(r);
        return ResponseEntity.ok(Result.ok(r));
    }
}
