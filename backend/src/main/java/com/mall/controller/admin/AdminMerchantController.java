package com.mall.controller.admin;

import com.mall.common.Role;
import com.mall.dto.Result;
import com.mall.entity.Merchant;
import com.mall.entity.Product;
import com.mall.entity.User;
import com.mall.repository.MerchantRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/merchant")
@RequiredArgsConstructor
/** 管理端商家管理接口：商家 CRUD 与商家账号绑定。 */
public class AdminMerchantController {

    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /** 查询商家列表，并附带所属商家账号信息。 */
    @GetMapping
    public Result<?> list() {
        List<Merchant> merchants = merchantRepository.findAll();
        List<Map<String, Object>> data = merchants.stream().map(m -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", m.getId());
            row.put("name", m.getName());
            row.put("description", m.getDescription());
            row.put("enabled", m.getEnabled());
            row.put("logo", m.getLogo());
            row.put("contactPhone", m.getContactPhone());
            row.put("contactPerson", m.getContactPerson());
            row.put("createdAt", m.getCreatedAt());
            row.put("updatedAt", m.getUpdatedAt());

            List<User> owners = userRepository.findByMerchantId(m.getId());
            if (owners != null && !owners.isEmpty()) {
                User u = owners.get(0);
                row.put("ownerUserId", u.getId());
                row.put("ownerUsername", u.getUsername());
                row.put("ownerNickname", u.getNickname());
            } else {
                row.put("ownerUserId", null);
                row.put("ownerUsername", null);
                row.put("ownerNickname", null);
            }
            return row;
        }).toList();
        return Result.ok(data);
    }

    /** 新建商家。 */
    @PostMapping
    public Result<?> create(@RequestBody Merchant merchant) {
        merchant.setId(null);
        return Result.ok(merchantRepository.save(merchant));
    }

    /** 更新商家信息。 */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Merchant merchant) {
        merchant.setId(id);
        return Result.ok(merchantRepository.save(merchant));
    }

    /**
     * 绑定/解绑店铺所属商家账号（sys_user.merchant_id）。
     * ownerUserId 传 null 表示解绑该店铺的所有商家账号。
     */
    @PutMapping("/{id}/owner")
    public Result<?> setOwner(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Merchant> mOpt = merchantRepository.findById(id);
        if (mOpt.isEmpty()) return Result.fail("商家不存在");

        Long ownerUserId = body.get("ownerUserId") == null ? null : Long.valueOf(body.get("ownerUserId").toString());

        // 先解绑该店铺当前所有绑定账号（保证唯一）
        List<User> bound = userRepository.findByMerchantId(id);
        if (bound != null && !bound.isEmpty()) {
            for (User u : bound) {
                // 如果等会儿还要绑定同一个用户，就先跳过
                if (ownerUserId != null && ownerUserId.equals(u.getId())) continue;
                u.setMerchantId(null);
            }
            userRepository.saveAll(bound);
        }

        if (ownerUserId == null) {
            return Result.ok(null);
        }

        Optional<User> uOpt = userRepository.findById(ownerUserId);
        if (uOpt.isEmpty()) return Result.fail("所属用户不存在");
        User u = uOpt.get();
        if (u.getRole() != Role.MERCHANT) return Result.fail("该用户不是商家角色");
        u.setMerchantId(id);
        userRepository.save(u);
        return Result.ok(null);
    }

    /** 查询单个商家详情。 */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        Optional<Merchant> m = merchantRepository.findById(id);
        return m.map(merchant -> Result.ok(merchant))
                .orElse(Result.fail("商家不存在"));
    }

    /** 删除商家。 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        merchantRepository.deleteById(id);
        return Result.ok(null);
    }

    /**
     * 恢复该商家名下商品：批量设为“审核通过 + 上架”。
     * 用于误操作/数据异常导致商品被统一下架的应急恢复。
     */
    @PostMapping("/{id}/restore-products")
    public Result<?> restoreProducts(@PathVariable Long id) {
        Optional<Merchant> mOpt = merchantRepository.findById(id);
        if (mOpt.isEmpty()) return Result.fail("商家不存在");

        List<Product> products = productRepository.findByMerchantId(id);
        if (products == null || products.isEmpty()) {
            return Result.ok(0);
        }
        for (Product p : products) {
            p.setReviewStatus("APPROVED");
            p.setReviewReason(null);
            p.setOnSale(true);
        }
        productRepository.saveAll(products);
        return Result.ok(products.size());
    }
}
