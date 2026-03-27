package com.mall.controller.merchant;

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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/merchant/review")
@RequiredArgsConstructor
/** 运营评价管理接口：查看、删除用户评价 */
public class MerchantReviewController {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /** 获取当前运营 ID（由登录用户映射）。 */
    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非运营账号");
        return u.getMerchantId();
    }

    /** 分页查询当前运营旗下所有商品的评价 */
    @GetMapping
    public Result<?> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer minRating) {
        Long merchantId = currentMerchantId(auth);

        // 获取该运营的所有商品ID
        List<Product> products = productRepository.findByMerchantId(merchantId, PageRequest.of(0, 10000)).getContent();
        List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Result.ok(new PageImpl<>(List.of(), PageRequest.of(page, size), 0));
        }

        // 获取这些商品的所有评价
        List<ProductReview> allReviews = productReviewRepository.findAll().stream()
                .filter(r -> productIds.contains(r.getProductId()))
                .collect(Collectors.toList());

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

        Page<ProductReview> result = new PageImpl<>(pageContent, PageRequest.of(page, size), allReviews.size());
        return Result.ok(result);
    }

    /** 查询单个商品的所有评价 */
    @GetMapping("/product/{productId}")
    public Result<?> getByProduct(
            Authentication auth,
            @PathVariable Long productId) {
        Long merchantId = currentMerchantId(auth);

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty() || !productOpt.get().getMerchantId().equals(merchantId)) {
            return Result.fail("商品不存在或无权限操作");
        }

        List<ProductReview> reviews = productReviewRepository
                .findByProductIdOrderByCreatedAtDesc(productId, PageRequest.of(0, 1000))
                .getContent();

        return Result.ok(reviews);
    }

    /** 删除单条评价 */
    @DeleteMapping("/{reviewId}")
    public Result<?> delete(
            Authentication auth,
            @PathVariable Long reviewId) {
        Long merchantId = currentMerchantId(auth);

        Optional<ProductReview> reviewOpt = productReviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            return Result.fail("评价不存在");
        }

        ProductReview review = reviewOpt.get();
        Optional<Product> productOpt = productRepository.findById(review.getProductId());
        if (productOpt.isEmpty() || !productOpt.get().getMerchantId().equals(merchantId)) {
            return Result.fail("无权限删除此评价");
        }

        productReviewRepository.deleteById(reviewId);
        return Result.ok("评价已删除");
    }

    /** 批量删除评价 */
    @PostMapping("/batch-delete")
    public Result<?> batchDelete(
            Authentication auth,
            @RequestBody List<Long> reviewIds) {
        Long merchantId = currentMerchantId(auth);

        int deleted = 0;
        for (Long reviewId : reviewIds) {
            Optional<ProductReview> reviewOpt = productReviewRepository.findById(reviewId);
            if (reviewOpt.isPresent()) {
                ProductReview review = reviewOpt.get();
                Optional<Product> productOpt = productRepository.findById(review.getProductId());
                if (productOpt.isPresent() && productOpt.get().getMerchantId().equals(merchantId)) {
                    productReviewRepository.deleteById(reviewId);
                    deleted++;
                }
            }
        }

        return Result.ok("成功删除 " + deleted + " 条评价");
    }
}
