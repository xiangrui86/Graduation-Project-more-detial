package com.mall.service;

import com.mall.entity.Product;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/** 商品服务：提供管理端与用户端的商品查询与维护能力。 */
public class ProductService {

    private final ProductRepository productRepository;

    /** 管理端商品详情查询。 */
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    /** 用户侧商品详情：仅返回“上架且商家启用”的商品 */
    public Optional<Product> getPublicById(Long id) {
        return productRepository.findPublicById(id);
    }

    /** 管理端商品分页列表（仅上架商品）。 */
    public Page<Product> listAll(Pageable pageable) {
        return productRepository.findByOnSaleTrue(pageable);
    }

    /** 管理端按分类查询商品。 */
    public Page<Product> listByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndOnSaleTrue(categoryId, pageable);
    }

    /** 用户侧商品列表：仅返回“上架且商家启用”的商品 */
    public Page<Product> listPublicAll(Pageable pageable) {
        return productRepository.findPublicOnSale(pageable);
    }

    /** 用户侧分类商品列表：仅返回“上架且商家启用”的商品 */
    public Page<Product> listPublicByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findPublicByCategory(categoryId, pageable);
    }

    /** 商家维度商品查询。 */
    public Page<Product> listByMerchant(Long merchantId, Pageable pageable) {
        return productRepository.findByMerchantIdAndOnSaleTrue(merchantId, pageable);
    }

    /** 新品列表。 */
    public List<Product> newArrivals(int size) {
        return productRepository.findByOnSaleTrueAndIsNewTrueOrderByCreatedAtDesc(
                PageRequest.of(0, size));
    }

    /** 销量排行。 */
    public List<Product> salesRanking(int size) {
        return productRepository.findByOnSaleTrueOrderBySalesDesc(
                PageRequest.of(0, size));
    }

    /** 用户侧新品：仅返回“上架且商家启用”的商品 */
    public List<Product> publicNewArrivals(int size) {
        return productRepository.findPublicNewArrivals(PageRequest.of(0, size));
    }

    /** 用户侧销量排行：仅返回“上架且商家启用”的商品 */
    public List<Product> publicSalesRanking(int size) {
        return productRepository.findPublicSalesRank(PageRequest.of(0, size));
    }

    /** 保存商品。 */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /** 删除商品。 */
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
