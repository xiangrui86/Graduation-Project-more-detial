package com.mall.service;

import com.mall.entity.Product;
import com.mall.repository.CategoryRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/** 商品服务：提供管理端与用户端的商品查询与维护能力。 */
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /** 管理端商品详情查询。 */
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    /** 用户侧商品详情：仅返回“上架且商家启用”的商品 */
    public Optional<Product> getPublicById(Long id) {
        return productRepository.findPublicById(id);
    }

    /** 管理端商品分页列表。 */
    public Page<Product> listAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /** 管理端按分类查询商品。 */
    public Page<Product> listByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndOnSaleTrue(categoryId, pageable);
    }

    /** 用户侧商品列表：仅返回“上架且商家启用”且审核通过的商品 */
    public Page<Product> listPublicAll(Pageable pageable) {
        return productRepository.findPublicOnSale(pageable);
    }

    /** 用户侧分类商品列表：仅返回“上架且商家启用”且审核通过的商品 */
    public Page<Product> listPublicByCategory(Long categoryId, Pageable pageable) {
        List<Long> categoryIds = collectCategoryIds(categoryId);
        return productRepository.findPublicByCategoryIds(categoryIds, pageable);
    }

    private List<Long> collectCategoryIds(Long categoryId) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        collectDescendantCategoryIds(categoryId, categoryIds);
        return categoryIds;
    }

    private void collectDescendantCategoryIds(Long parentId, List<Long> ids) {
        List<com.mall.entity.Category> children = categoryRepository.findByParentIdOrderBySortOrderAsc(parentId);
        for (com.mall.entity.Category child : children) {
            ids.add(child.getId());
            collectDescendantCategoryIds(child.getId(), ids);
        }
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

    /** 用户侧搜索商品：仅返回“上架且商家启用”的商品 */
    public Page<Product> searchPublicProducts(String keyword, Pageable pageable) {
        return productRepository.findPublicByNameContaining(keyword, pageable);
    }

    /** 保存商品。 */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /** 删除商品。 */
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /** 库存管理：按商家查询商品库存（包含下架商品）。 */
    public Page<Product> listInventoryByMerchant(Long merchantId, String keyword, Integer stockStatus, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (stockStatus != null) {
                if (stockStatus == 0) { // 缺货
                    return productRepository.findByMerchantIdAndNameContainingAndStock(merchantId, keyword, 0, pageable);
                } else if (stockStatus == 1) { // 低库存
                    return productRepository.findByMerchantIdAndNameContainingAndStockBetween(merchantId, keyword, 1, 10, pageable);
                } else if (stockStatus == 2) { // 正常
                    return productRepository.findByMerchantIdAndNameContainingAndStockGreaterThan(merchantId, keyword, 10, pageable);
                }
            }
            return productRepository.findByMerchantIdAndNameContaining(merchantId, keyword, pageable);
        } else {
            if (stockStatus != null) {
                if (stockStatus == 0) { // 缺货
                    return productRepository.findByMerchantIdAndStock(merchantId, 0, pageable);
                } else if (stockStatus == 1) { // 低库存
                    return productRepository.findByMerchantIdAndStockBetween(merchantId, 1, 10, pageable);
                } else if (stockStatus == 2) { // 正常
                    return productRepository.findByMerchantIdAndStockGreaterThan(merchantId, 10, pageable);
                }
            }
            return productRepository.findByMerchantId(merchantId, pageable);
        }
    }

    /** 获取低库存商品。 */
    public List<Product> getLowStockProducts(Long merchantId, int threshold) {
        return productRepository.findByMerchantIdAndStockLessThanEqual(merchantId, threshold);
    }
}
