package com.mall.controller.admin;

import com.mall.dto.ProductCreateRequest;
import com.mall.dto.Result;
import com.mall.entity.Category;
import com.mall.entity.Product;
import com.mall.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

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

    /** 创建商品（管理员创建商品默认直接上架通过） */
    @PostMapping
    public Result<?> create(@RequestBody ProductCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return Result.fail("商品名称不能为空");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return Result.fail("商品价格必须大于0");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            return Result.fail("库存数量不能为负数");
        }

        Long categoryId = request.getCategoryId();
        if (request.getCategoryName() != null && !request.getCategoryName().isEmpty()) {
            Optional<Category> existing = categoryRepository.findByNameAndParentIdIsNull(request.getCategoryName());
            if (existing.isPresent()) {
                categoryId = existing.get().getId();
            } else {
                int maxSortOrder = (int) categoryRepository.count() + 1;
                Category newCategory = Category.builder()
                        .name(request.getCategoryName())
                        .parentId(null)
                        .sortOrder(maxSortOrder)
                        .build();
                Category savedCategory = categoryRepository.save(newCategory);
                categoryId = savedCategory.getId();
            }
        }

        String detailImages = null;
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            detailImages = String.join(",", request.getImages());
        } else if (request.getDetailImages() != null && !request.getDetailImages().isEmpty()) {
            detailImages = request.getDetailImages();
        }

        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .detailDescription(request.getDetailDescription())
                .categoryId(categoryId)
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .stock(request.getStock())
                .unit(request.getUnit() != null ? request.getUnit() : "件")
                .image(request.getImage())
                .detailImages(detailImages)
                .brand(request.getBrand())
                .attributes(request.getAttributes())
                .isNew(request.getIsNew() != null ? request.getIsNew() : false)
                .onSale(request.getOnSale() != null ? request.getOnSale() : true)
                .reviewStatus("APPROVED")
                .reviewReason(null)
                .merchantId(0L)
                .sales(0)
                .build();
        return Result.ok(productService.save(product));
    }

    /** 更新商品（管理员编辑商品默认直接通过审核） */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ProductCreateRequest request) {
        Optional<Product> existing = productService.getById(id);
        if (existing.isEmpty()) {
            return Result.fail("商品不存在");
        }

        Long categoryId = request.getCategoryId();
        if (request.getCategoryName() != null && !request.getCategoryName().isEmpty()) {
            Optional<Category> existingCategory = categoryRepository.findByNameAndParentIdIsNull(request.getCategoryName());
            if (existingCategory.isPresent()) {
                categoryId = existingCategory.get().getId();
            } else {
                int maxSortOrder = (int) categoryRepository.count() + 1;
                Category newCategory = Category.builder()
                        .name(request.getCategoryName())
                        .parentId(null)
                        .sortOrder(maxSortOrder)
                        .build();
                Category savedCategory = categoryRepository.save(newCategory);
                categoryId = savedCategory.getId();
            }
        }

        String detailImages = null;
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            detailImages = String.join(",", request.getImages());
        } else if (request.getDetailImages() != null && !request.getDetailImages().isEmpty()) {
            detailImages = request.getDetailImages();
        }

        Product product = existing.get();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setDetailDescription(request.getDetailDescription());
        product.setCategoryId(categoryId);
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setUnit(request.getUnit() != null ? request.getUnit() : "件");
        product.setBrand(request.getBrand());
        product.setImage(request.getImage());
        product.setDetailImages(detailImages);
        product.setAttributes(request.getAttributes());
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        product.setOnSale(request.getOnSale() != null ? request.getOnSale() : product.getOnSale());
        product.setReviewStatus("APPROVED");
        product.setReviewReason(null);

        return Result.ok(productService.save(product));
    }

    /** 删除商品 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Optional<Product> existing = productService.getById(id);
        if (existing.isEmpty()) {
            return Result.fail("商品不存在");
        }
        productService.delete(id);
        return Result.ok(null);
    }
}
