package com.mall.controller.user;

import com.mall.dto.Result;
import com.mall.entity.CartItem;
import com.mall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
/** 用户购物车接口：查询、添加、改数量、删除。 */
public class UserCartController {

    private final CartService cartService;

    /** 获取当前登录用户 ID。 */
    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    /** 查询当前用户购物车。 */
    @GetMapping
    public ResponseEntity<Result<?>> list(Authentication auth) {
        List<CartItem> list = cartService.findByUserId(currentUserId(auth));
        return ResponseEntity.ok(Result.ok(list));
    }

    /** 添加商品到购物车。 */
    @PostMapping("/add")
    public ResponseEntity<Result<?>> add(Authentication auth, @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? ((Number) body.get("quantity")).intValue() : 1;
        try {
            CartItem item = cartService.add(currentUserId(auth), productId, quantity);
            return ResponseEntity.ok(Result.ok(item));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    /** 更新购物车商品数量。 */
    @PutMapping("/quantity")
    public ResponseEntity<Result<?>> updateQuantity(Authentication auth, @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = ((Number) body.get("quantity")).intValue();
        try {
            cartService.updateQuantity(currentUserId(auth), productId, quantity);
            return ResponseEntity.ok(Result.ok(null));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    /** 从购物车移除商品。 */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Result<?>> remove(Authentication auth, @PathVariable Long productId) {
        cartService.remove(currentUserId(auth), productId);
        return ResponseEntity.ok(Result.ok(null));
    }
}
