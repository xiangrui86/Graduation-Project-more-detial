package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.repository.ProductRepository;
import com.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
/** 管理端商品接口：商品列表、详情、更新与删除。 */
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    /** 分页查询商品列表。 */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(productRepository.findAll(PageRequest.of(page, size)));
    }

    /** 查询商品详情。 */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return productService.getById(id)
                .map(p -> Result.ok(p))
                .orElse(Result.fail("商品不存在"));
    }

    /** 更新商品信息。 */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return Result.ok(productService.save(product));
    }

    /** 删除商品。 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok(null);
    }
}
