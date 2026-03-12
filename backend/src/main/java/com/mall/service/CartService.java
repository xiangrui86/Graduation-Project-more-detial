package com.mall.service;

import com.mall.entity.CartItem;
import com.mall.entity.Product;
import com.mall.repository.CartItemRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public List<CartItem> findByUserId(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem add(Long userId, Long productId, int quantity) {
        Optional<Product> p = productRepository.findById(productId);
        if (p.isEmpty() || !p.get().getOnSale()) throw new RuntimeException("商品不存在或已下架");
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            CartItem c = existing.get();
            c.setQuantity(c.getQuantity() + quantity);
            return cartItemRepository.save(c);
        }
        CartItem c = CartItem.builder().userId(userId).productId(productId).quantity(quantity).build();
        return cartItemRepository.save(c);
    }

    @Transactional
    public void updateQuantity(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            cartItemRepository.deleteByUserIdAndProductId(userId, productId);
            return;
        }
        cartItemRepository.findByUserIdAndProductId(userId, productId).ifPresent(c -> {
            c.setQuantity(quantity);
            cartItemRepository.save(c);
        });
    }

    @Transactional
    public void remove(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
