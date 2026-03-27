package com.mall.controller.merchant;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.entity.User;
import com.mall.repository.UserRepository;
import com.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/merchant/inventory")
@RequiredArgsConstructor
/** 运营库存管理接口：库存查询、调整、入库、出库记录。 */
public class MerchantInventoryController {

    private final ProductService productService;
    private final UserRepository userRepository;

    /** 获取当前运营 ID（由登录用户映射）。 */
    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非运营账号");
        return u.getMerchantId();
    }

    /** 分页查询当前运营的商品库存。 */
    @GetMapping
    public Result<?> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer stockStatus) {
        Long merchantId = currentMerchantId(auth);
        return Result.ok(
                productService.listInventoryByMerchant(merchantId, keyword, stockStatus, PageRequest.of(page, size)));
    }

    /** 调整商品库存。 */
    @PutMapping("/{productId}/stock")
    public Result<?> updateStock(
            Authentication auth,
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> request) {
        Long merchantId = currentMerchantId(auth);
        Integer newStock = request.get("stock");
        if (newStock == null || newStock < 0) {
            return Result.fail("库存数量必须大于等于0");
        }

        Optional<Product> productOpt = productService.getById(productId);
        if (productOpt.isEmpty() || !productOpt.get().getMerchantId().equals(merchantId)) {
            return Result.fail("商品不存在或无权限操作");
        }

        Product product = productOpt.get();
        Integer oldStock = product.getStock();
        product.setStock(newStock);
        productService.save(product);

        return Result.ok(Map.of(
                "productId", productId,
                "oldStock", oldStock,
                "newStock", newStock,
                "change", newStock - oldStock
        ));
    }

    /** 批量调整库存。 */
    @PutMapping("/batch-stock")
    public Result<?> batchUpdateStock(
            Authentication auth,
            @RequestBody Map<Long, Integer> stockUpdates) {
        Long merchantId = currentMerchantId(auth);

        for (Map.Entry<Long, Integer> entry : stockUpdates.entrySet()) {
            Long productId = entry.getKey();
            Integer newStock = entry.getValue();

            if (newStock == null || newStock < 0) {
                return Result.fail("商品ID " + productId + " 的库存数量必须大于等于0");
            }

            Optional<Product> productOpt = productService.getById(productId);
            if (productOpt.isEmpty() || !productOpt.get().getMerchantId().equals(merchantId)) {
                return Result.fail("商品ID " + productId + " 不存在或无权限操作");
            }
        }

        // 执行批量更新
        for (Map.Entry<Long, Integer> entry : stockUpdates.entrySet()) {
            Long productId = entry.getKey();
            Integer newStock = entry.getValue();

            Product product = productService.getById(productId).get();
            product.setStock(newStock);
            productService.save(product);
        }

        return Result.ok("批量更新库存成功");
    }

    /** 获取库存预警商品（库存少于设定值）。 */
    @GetMapping("/warnings")
    public Result<?> getStockWarnings(
            Authentication auth,
            @RequestParam(defaultValue = "10") int threshold) {
        Long merchantId = currentMerchantId(auth);
        return Result.ok(productService.getLowStockProducts(merchantId, threshold));
    }
}