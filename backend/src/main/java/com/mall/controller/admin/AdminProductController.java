package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.repository.ProductRepository;
import com.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
/** 管理员商品审核接口 */
public class AdminProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    /** 分页查询商品，可按审核状态过滤 */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String reviewStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products;
        if (reviewStatus == null || reviewStatus.isBlank()) {
            products = productService.listAll(pageable);
        } else {
            products = productRepository.findByReviewStatus(reviewStatus, pageable);
        }
        return Result.ok(products);
    }

    /** 查询单个商品详情 */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        Optional<Product> product = productService.getById(id);
        return product.map(Result::ok).orElseGet(() -> Result.fail("商品不存在"));
    }

    /** 审核通过 */
    @PostMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getById(id);
        if (productOpt.isEmpty()) {
            return Result.fail("商品不存在");
        }
        Product product = productOpt.get();
        product.setReviewStatus("APPROVED");
        product.setReviewReason(null);
        product.setOnSale(true);
        return Result.ok(productService.save(product));
    }

    /** 审核不通过 */
    @PostMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Product> productOpt = productService.getById(id);
        if (productOpt.isEmpty()) {
            return Result.fail("商品不存在");
        }
        Product product = productOpt.get();
        String reason = body.getOrDefault("reason", "商品未通过审核，请补充信息后重新提交");
        product.setReviewStatus("REJECTED");
        product.setReviewReason(reason.trim().isEmpty() ? "商品未通过审核，请补充信息后重新提交" : reason.trim());
        product.setOnSale(false);
        return Result.ok(productService.save(product));
    }
}
