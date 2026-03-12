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
public class ProductService {

    private final ProductRepository productRepository;

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> listAll(Pageable pageable) {
        return productRepository.findByOnSaleTrue(pageable);
    }

    public Page<Product> listByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndOnSaleTrue(categoryId, pageable);
    }

    public Page<Product> listByMerchant(Long merchantId, Pageable pageable) {
        return productRepository.findByMerchantIdAndOnSaleTrue(merchantId, pageable);
    }

    public List<Product> newArrivals(int size) {
        return productRepository.findByOnSaleTrueAndIsNewTrueOrderByCreatedAtDesc(
                PageRequest.of(0, size));
    }

    public List<Product> salesRanking(int size) {
        return productRepository.findByOnSaleTrueOrderBySalesDesc(
                PageRequest.of(0, size));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
