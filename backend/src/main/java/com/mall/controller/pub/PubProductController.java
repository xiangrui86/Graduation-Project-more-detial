package com.mall.controller.pub;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.service.CollaborativeFilteringService;
import com.mall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/** 公开接口：商城首页、全部商品、分类、新品、销量排行、猜您想买（需登录时前端传 userId） */
@RestController
@RequestMapping("/pub")
@RequiredArgsConstructor
public class PubProductController {

    private final ProductService productService;
    private final CollaborativeFilteringService collaborativeFilteringService;

    /** 分页查询公开商品列表，可按分类过滤，可搜索，可排序。 */
    @GetMapping("/products")
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sortSpec = buildSort(sort, direction);
        PageRequest pageable = sortSpec != null
                ? PageRequest.of(page, size, sortSpec)
                : PageRequest.of(page, size);

        if (search != null && !search.trim().isEmpty()) {
            return Result.ok(productService.searchPublicProducts(search.trim(), pageable));
        }
        if (categoryId != null) {
            return Result.ok(productService.listPublicByCategory(categoryId, pageable));
        }
        return Result.ok(productService.listPublicAll(pageable));
    }

    /** 根据前端传参构建 JPA Sort 对象。 */
    private Sort buildSort(String sort, String direction) {
        if (sort == null || sort.trim().isEmpty()) return null;
        Sort.Direction dir = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        // 映射前端字段名 → 实体字段名
        String field = switch (sort) {
            case "price" -> "price";
            case "sales" -> "sales";
            case "createdAt" -> "createdAt";
            default -> null;
        };
        return field != null ? Sort.by(dir, field) : null;
    }

    /** 查询公开商品详情。 */
    @GetMapping("/products/{id}")
    public Result<?> get(@PathVariable Long id) {
        Optional<Product> p = productService.getPublicById(id);
        return p.map(pr -> Result.ok(pr))
                .orElse(Result.fail("商品不存在"));
    }

    /** 查询公开新品列表。 */
    @GetMapping("/products/new")
    public Result<?> newArrivals(@RequestParam(defaultValue = "10") int size) {
        List<Product> list = productService.publicNewArrivals(size);
        return Result.ok(list);
    }

    /** 查询公开销量排行。 */
    @GetMapping("/products/rank")
    public Result<?> salesRank(@RequestParam(defaultValue = "10") int size) {
        List<Product> list = productService.publicSalesRanking(size);
        return Result.ok(list);
    }

    /** 猜您想买：协同过滤推荐，需传 userId（登录用户） */
    @GetMapping("/products/recommend")
    public Result<?> recommend(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "20") int size) {
        List<Product> list = collaborativeFilteringService.recommendForUser(userId);
        if (list.size() > size) list = list.subList(0, size);
        return Result.ok(list);
    }
}
