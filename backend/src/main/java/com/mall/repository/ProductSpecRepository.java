package com.mall.repository;

import com.mall.entity.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {
    List<ProductSpec> findByProductIdOrderBySortOrderAsc(Long productId);
}
