package com.mall.controller.merchant;

import com.mall.dto.ProductSpecRequest;
import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.entity.ProductSpec;
import com.mall.entity.User;
import com.mall.repository.ProductRepository;
import com.mall.repository.ProductSpecRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/merchant/product-specs")
@RequiredArgsConstructor
public class MerchantProductSpecController {

    private final ProductSpecRepository productSpecRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非商家账号");
        return u.getMerchantId();
    }

    private Optional<Product> findOwnedProduct(Long merchantId, Long productId) {
        return productRepository.findById(productId)
                .filter(p -> p.getMerchantId().equals(merchantId));
    }

    @GetMapping("/product/{productId}")
    public Result<?> list(Authentication auth, @PathVariable Long productId) {
        Long merchantId = currentMerchantId(auth);
        if (findOwnedProduct(merchantId, productId).isEmpty()) {
            return Result.fail("商品不存在或无权限操作");
        }
        List<ProductSpec> specs = productSpecRepository.findByProductIdOrderBySortOrderAsc(productId);
        return Result.ok(specs);
    }

    @PostMapping("/{productId}")
    public Result<?> create(Authentication auth, @PathVariable Long productId,
                            @RequestBody ProductSpecRequest request) {
        Long merchantId = currentMerchantId(auth);
        if (findOwnedProduct(merchantId, productId).isEmpty()) {
            return Result.fail("商品不存在或无权限操作");
        }
        ProductSpec spec = ProductSpec.builder()
                .productId(productId)
                .specName(request.getSpecName())
                .specValue(request.getSpecValue())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .priceDelta(request.getPriceDelta())
                .image(request.getImage())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();
        return Result.ok(productSpecRepository.save(spec));
    }

    @PutMapping("/{specId}")
    public Result<?> update(Authentication auth, @PathVariable Long specId,
                            @RequestBody ProductSpecRequest request) {
        Long merchantId = currentMerchantId(auth);
        Optional<ProductSpec> existing = productSpecRepository.findById(specId);
        if (existing.isEmpty() || findOwnedProduct(merchantId, existing.get().getProductId()).isEmpty()) {
            return Result.fail("规格不存在或无权限操作");
        }
        ProductSpec spec = existing.get();
        spec.setSpecName(request.getSpecName());
        spec.setSpecValue(request.getSpecValue());
        spec.setStock(request.getStock() != null ? request.getStock() : spec.getStock());
        spec.setPriceDelta(request.getPriceDelta());
        spec.setImage(request.getImage());
        spec.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : spec.getSortOrder());
        spec.setEnabled(request.getEnabled() != null ? request.getEnabled() : spec.getEnabled());
        return Result.ok(productSpecRepository.save(spec));
    }

    @DeleteMapping("/{specId}")
    public Result<?> delete(Authentication auth, @PathVariable Long specId) {
        Long merchantId = currentMerchantId(auth);
        Optional<ProductSpec> existing = productSpecRepository.findById(specId);
        if (existing.isEmpty() || findOwnedProduct(merchantId, existing.get().getProductId()).isEmpty()) {
            return Result.fail("规格不存在或无权限操作");
        }
        productSpecRepository.deleteById(specId);
        return Result.ok(null);
    }
}
