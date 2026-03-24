package com.mall.controller.user;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/favorite")
@RequiredArgsConstructor
/** 用户收藏接口：收藏列表、收藏状态、增删收藏。 */
public class UserFavoriteController {

    private final FavoriteService favoriteService;

    /** 获取当前登录用户 ID。 */
    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    /** 查询收藏商品列表。 */
    @GetMapping
    public ResponseEntity<Result<?>> list(Authentication auth) {
        List<Product> list = favoriteService.findByUserId(currentUserId(auth));
        return ResponseEntity.ok(Result.ok(list));
    }

    /** 检查商品是否已收藏。 */
    @GetMapping("/check")
    public ResponseEntity<Result<?>> check(Authentication auth, @RequestParam Long productId) {
        boolean isFav = favoriteService.isFavorite(currentUserId(auth), productId);
        return ResponseEntity.ok(Result.ok(Map.of("favorite", isFav)));
    }

    /** 新增收藏。 */
    @PostMapping("/add")
    public ResponseEntity<Result<?>> add(Authentication auth, @RequestBody Map<String, Long> body) {
        Long productId = body.get("productId");
        try {
            favoriteService.add(currentUserId(auth), productId);
            return ResponseEntity.ok(Result.ok(null));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    /** 取消收藏。 */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Result<?>> remove(Authentication auth, @PathVariable Long productId) {
        favoriteService.remove(currentUserId(auth), productId);
        return ResponseEntity.ok(Result.ok(null));
    }
}
