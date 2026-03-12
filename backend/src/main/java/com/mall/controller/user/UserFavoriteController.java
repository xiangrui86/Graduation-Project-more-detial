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
public class UserFavoriteController {

    private final FavoriteService favoriteService;

    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping
    public ResponseEntity<Result<?>> list(Authentication auth) {
        List<Product> list = favoriteService.findByUserId(currentUserId(auth));
        return ResponseEntity.ok(Result.ok(list));
    }

    @GetMapping("/check")
    public ResponseEntity<Result<?>> check(Authentication auth, @RequestParam Long productId) {
        boolean isFav = favoriteService.isFavorite(currentUserId(auth), productId);
        return ResponseEntity.ok(Result.ok(Map.of("favorite", isFav)));
    }

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

    @DeleteMapping("/{productId}")
    public ResponseEntity<Result<?>> remove(Authentication auth, @PathVariable Long productId) {
        favoriteService.remove(currentUserId(auth), productId);
        return ResponseEntity.ok(Result.ok(null));
    }
}
