package com.mall.repository;

import com.mall.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByMerchantId(Long merchantId, Pageable pageable);

    Page<Product> findByOnSaleTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndOnSaleTrue(Long categoryId, Pageable pageable);

    Page<Product> findByMerchantIdAndOnSaleTrue(Long merchantId, Pageable pageable);

    List<Product> findByOnSaleTrueOrderByCreatedAtDesc(Pageable pageable);

    List<Product> findByOnSaleTrueOrderBySalesDesc(Pageable pageable);

    List<Product> findByOnSaleTrueAndIsNewTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.onSale = true AND p.id IN :ids")
    List<Product> findByIdInAndOnSaleTrue(List<Long> ids);
}
