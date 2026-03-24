package com.mall.controller.merchant;

import com.mall.dto.ProductCreateRequest;
import com.mall.dto.Result;
import com.mall.entity.Category;
import com.mall.entity.Product;
import com.mall.entity.User;
import com.mall.repository.CategoryRepository;
import com.mall.repository.UserRepository;
import com.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/merchant/product")
@RequiredArgsConstructor
/** 商家商品接口：商品列表、详情、新增、更新、删除。 */
public class MerchantProductController {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /** 获取当前商家 ID（由登录用户映射）。 */
    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非商家账号");
        return u.getMerchantId();
    }

    /** 分页查询当前商家的商品。 */
    @GetMapping
    public Result<?> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(
                productService.listByMerchant(currentMerchantId(auth), PageRequest.of(page, size)));
    }

    /** 查询当前商家单个商品详情。 */
    @GetMapping("/{id}")
    public Result<?> get(Authentication auth, @PathVariable Long id) {
        Optional<Product> p = productService.getById(id);
        if (p.isEmpty() || !p.get().getMerchantId().equals(currentMerchantId(auth))) {
            return Result.fail("商品不存在");
        }
        return Result.ok(p.get());
    }

    /** 创建商品（支持按分类名自动创建分类）。 */
    @PostMapping
    public Result<?> create(Authentication auth, @RequestBody ProductCreateRequest request) {
        // 处理分类：如果提供了自定义分类名称，则创建/查找该分类
        Long categoryId = request.getCategoryId();
        if (request.getCategoryName() != null && !request.getCategoryName().isEmpty()) {
            Optional<Category> existing = categoryRepository.findByNameAndParentIdIsNull(request.getCategoryName());
            if (existing.isPresent()) {
                categoryId = existing.get().getId();
            } else {
                // 创建新分类
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

        // 创建产品
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(categoryId)
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .stock(request.getStock())
                .image(request.getImage())
                .isNew(request.getIsNew() != null ? request.getIsNew() : false)
                .onSale(request.getOnSale() != null ? request.getOnSale() : true)
                .merchantId(currentMerchantId(auth))
                .sales(0)
                .build();
        return Result.ok(productService.save(product));
    }

    /** 更新商品。 */
    @PutMapping("/{id}")
    public Result<?> update(Authentication auth, @PathVariable Long id, @RequestBody Product product) {
        Optional<Product> existing = productService.getById(id);
        if (existing.isEmpty() || !existing.get().getMerchantId().equals(currentMerchantId(auth))) {
            return Result.fail("商品不存在");
        }
        product.setId(id);
        product.setMerchantId(currentMerchantId(auth));
        product.setSales(existing.get().getSales());
        return Result.ok(productService.save(product));
    }

    /** 删除商品。 */
    @DeleteMapping("/{id}")
    public Result<?> delete(Authentication auth, @PathVariable Long id) {
        Optional<Product> p = productService.getById(id);
        if (p.isEmpty() || !p.get().getMerchantId().equals(currentMerchantId(auth))) {
            return Result.fail("商品不存在");
        }
        productService.delete(id);
        return Result.ok(null);
    }
}
