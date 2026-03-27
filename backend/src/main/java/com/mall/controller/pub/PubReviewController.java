package com.mall.controller.pub;

import com.mall.dto.Result;
import com.mall.entity.ProductReview;
import com.mall.entity.User;
import com.mall.repository.ProductReviewRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pub")
@RequiredArgsConstructor
/** 公共评价接口：按商品分页查询评价列表。 */
public class PubReviewController {

    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;

    /** 查询商品评价列表。 */
    @GetMapping("/reviews")
    public ResponseEntity<Result<?>> list(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductReview> reviews = productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId, PageRequest.of(page, size));
        // 为每条评价关联用户昵称
        List<Map<String, Object>> enrichedReviews = reviews.getContent().stream().map(review -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", review.getId());
            data.put("productId", review.getProductId());
            data.put("orderId", review.getOrderId());
            data.put("userId", review.getUserId());
            data.put("rating", review.getRating());
            data.put("content", review.getContent());
            data.put("createdAt", review.getCreatedAt());
            // 获取用户昵称，如果没有则使用用户名
            Optional<User> user = userRepository.findById(review.getUserId());
            if (user.isPresent()) {
                String nickname = user.get().getNickname();
                data.put("nickname", nickname != null && !nickname.isEmpty() ? nickname : user.get().getUsername());
            } else {
                data.put("nickname", "匿名用户");
            }
            return data;
        }).toList();
        
        return ResponseEntity.ok(Result.ok(new org.springframework.data.domain.PageImpl<>(
                enrichedReviews,
                reviews.getPageable(),
                reviews.getTotalElements()
        )));
    }
}

