package com.mall.service;

import com.mall.entity.Product;
import com.mall.repository.OrderItemRepository;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐算法：《猜您想买》
 * 若用户A购买并收货了 A、B、C，用户B 购买并收货了 A，则推荐用户B 购买 B、C。
 */
@Service
@RequiredArgsConstructor
public class CollaborativeFilteringService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    private static final int RECOMMEND_LIMIT = 20;
    private static final int MIN_COMMON_ITEMS = 1;

    /**
     * 为当前用户生成「猜您想买」推荐商品列表
     */
    public List<Product> recommendForUser(Long userId) {
        // 1. 当前用户已收货的商品 ID 列表
        List<Long> myReceivedProductIds = orderItemRepository.findReceivedProductIdsByUserId(userId);
        if (myReceivedProductIds.isEmpty()) {
            return fallbackBySales(PageRequest.of(0, RECOMMEND_LIMIT));
        }

        // 2. 其他用户的已收货订单 -> 商品 ID 集合
        Map<Long, Set<Long>> userToProducts = new HashMap<>();
        for (Object[] row : orderItemRepository.findOtherUsersReceivedProductsRaw(userId)) {
            Long uid = ((Number) row[0]).longValue();
            Long pid = ((Number) row[1]).longValue();
            userToProducts.computeIfAbsent(uid, k -> new HashSet<>()).add(pid);
        }

        Set<Long> mySet = new HashSet<>(myReceivedProductIds);
        Map<Long, Integer> productScore = new HashMap<>();

        for (Map.Entry<Long, Set<Long>> e : userToProducts.entrySet()) {
            Set<Long> otherSet = e.getValue();
            Set<Long> common = new HashSet<>(mySet);
            common.retainAll(otherSet);
            if (common.size() < MIN_COMMON_ITEMS) continue;

            for (Long pid : otherSet) {
                if (mySet.contains(pid)) continue;
                productScore.merge(pid, common.size(), Integer::sum);
            }
        }

        if (productScore.isEmpty()) {
            return fallbackBySales(PageRequest.of(0, RECOMMEND_LIMIT));
        }

        List<Long> recommendedIds = productScore.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(RECOMMEND_LIMIT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Product> list = productRepository.findByIdInAndOnSaleTrue(recommendedIds);
        Map<Long, Product> byId = list.stream().collect(Collectors.toMap(Product::getId, p -> p));
        return recommendedIds.stream().map(byId::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<Product> fallbackBySales(PageRequest pageRequest) {
        return productRepository.findByOnSaleTrueOrderBySalesDesc(pageRequest);
    }
}
