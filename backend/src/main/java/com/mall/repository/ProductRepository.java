package com.mall.repository;

import com.mall.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/** 商品仓储：封装管理端与公开端商品查询。 */
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

    // ===== 用户侧（公开接口）查询：需同时满足 商品上架 + 商家启用 =====

    @Query("""
            SELECT p FROM Product p
            WHERE p.onSale = true
              AND p.merchantId IN (SELECT m.id FROM Merchant m WHERE m.enabled = true)
            """)
    Page<Product> findPublicOnSale(Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.onSale = true
              AND p.categoryId = :categoryId
              AND p.merchantId IN (SELECT m.id FROM Merchant m WHERE m.enabled = true)
            """)
    Page<Product> findPublicByCategory(Long categoryId, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.onSale = true
              AND p.isNew = true
              AND p.merchantId IN (SELECT m.id FROM Merchant m WHERE m.enabled = true)
            ORDER BY p.createdAt DESC
            """)
    List<Product> findPublicNewArrivals(Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.onSale = true
              AND p.merchantId IN (SELECT m.id FROM Merchant m WHERE m.enabled = true)
            ORDER BY p.sales DESC
            """)
    List<Product> findPublicSalesRank(Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.onSale = true
              AND p.id IN :ids
              AND p.merchantId IN (SELECT m.id FROM Merchant m WHERE m.enabled = true)
            """)
    List<Product> findPublicByIdIn(List<Long> ids);

    @Query("""
            SELECT p FROM Product p
            WHERE p.id = :id
              AND p.onSale = true
              AND p.merchantId IN (SELECT m.id FROM Merchant m WHERE m.enabled = true)
            """)
    Optional<Product> findPublicById(Long id);
}
