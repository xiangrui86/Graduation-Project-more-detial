package com.mall.service;

import com.mall.entity.Favorite;
import com.mall.entity.Product;
import com.mall.repository.FavoriteRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    public List<Product> findByUserId(Long userId) {
        List<Favorite> list = favoriteRepository.findByUserId(userId);
        List<Long> ids = list.stream().map(Favorite::getProductId).toList();
        return productRepository.findAllById(ids);
    }

    public boolean isFavorite(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void add(Long userId, Long productId) {
        if (favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent()) return;
        if (productRepository.findById(productId).isEmpty()) throw new RuntimeException("商品不存在");
        favoriteRepository.save(Favorite.builder().userId(userId).productId(productId).build());
    }

    @Transactional
    public void remove(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
