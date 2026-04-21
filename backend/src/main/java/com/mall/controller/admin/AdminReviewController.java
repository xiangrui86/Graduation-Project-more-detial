package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.entity.ProductReview;
import com.mall.entity.User;
import com.mall.repository.ProductRepository;
import com.mall.repository.ProductReviewRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/review")
@RequiredArgsConstructor
/** 管理员评价管理接口：查看、删除用户评价 */
public class AdminReviewController {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /** 分页查询所有评价 */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer minRating) {

        // 获取所有评价
        List<ProductReview> allReviews = productReviewRepository.findAll();

        // 应用过滤条件
        if (productId != null) {
            allReviews = allReviews.stream()
                    .filter(r -> r.getProductId().equals(productId))
                    .collect(Collectors.toList());
        }
        if (minRating != null && minRating > 0) {
            allReviews = allReviews.stream()
                    .filter(r -> r.getRating() >= minRating)
                    .collect(Collectors.toList());
        } else if (minRating != null && minRating == -3) {
            // 低于3星的评价
            allReviews = allReviews.stream()
                    .filter(r -> r.getRating() < 3)
                    .collect(Collectors.toList());
        }

        // 按创建时间倒序排列
        allReviews = allReviews.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        // 分页
        int start = page * size;
        int end = Math.min(start + size, allReviews.size());
        List<ProductReview> pageContent = allReviews.subList(start, end);

        // 转换为包含商品和用户信息的数据
        List<Map<String, Object>> enrichedReviews = pageContent.stream()
                .map(review -> {
                    Map<String, Object> reviewData = new HashMap<>();
                    reviewData.put("id", review.getId());
                    reviewData.put("productId", review.getProductId());
                    reviewData.put("userId", review.getUserId());
                    reviewData.put("rating", review.getRating());
                    reviewData.put("content", review.getContent());
                    reviewData.put("createdAt", review.getCreatedAt());

                    // 获取商品信息
                    Optional<Product> productOpt = productRepository.findById(review.getProductId());
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        reviewData.put("productName", product.getName());
                        reviewData.put("productImage", product.getImage());
                        reviewData.put("productPrice", product.getPrice());
                    } else {
                        reviewData.put("productName", "商品已删除");
                        reviewData.put("productImage", null);
                        reviewData.put("productPrice", 0.0);
                    }

                    // 获取用户信息
                    Optional<User> userOpt = userRepository.findById(review.getUserId());
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        reviewData.put("userUsername", user.getUsername());
                        reviewData.put("userNickname", user.getNickname());
                    } else {
                        reviewData.put("userUsername", "用户已删除");
                        reviewData.put("userNickname", "用户已删除");
                    }

                    return reviewData;
                })
                .collect(Collectors.toList());

        Page<Map<String, Object>> result = new PageImpl<>(enrichedReviews, PageRequest.of(page, size), allReviews.size());
        return Result.ok(result);
    }

    /** 删除单条评价 */
    @DeleteMapping("/{reviewId}")
    public Result<?> delete(@PathVariable Long reviewId) {
        Optional<ProductReview> reviewOpt = productReviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            return Result.fail("评价不存在");
        }

        productReviewRepository.deleteById(reviewId);
        return Result.ok("评价已删除");
    }

    /** 批量删除评价 */
    @PostMapping("/batch-delete")
    public Result<?> batchDelete(@RequestBody List<Long> reviewIds) {
        int deleted = 0;
        for (Long reviewId : reviewIds) {
            if (productReviewRepository.existsById(reviewId)) {
                productReviewRepository.deleteById(reviewId);
                deleted++;
            }
        }

        return Result.ok("成功删除 " + deleted + " 条评价");
    }
}
