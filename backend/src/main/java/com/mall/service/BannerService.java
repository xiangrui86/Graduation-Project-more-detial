package com.mall.service;

import com.mall.dto.BannerDTO;
import com.mall.entity.Banner;
import com.mall.entity.Product;
import com.mall.repository.BannerRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;
    private final ProductRepository productRepository;

    public List<BannerDTO> listAll() {
        List<Banner> banners = bannerRepository.findAllByEnabledTrueOrderBySortOrderAsc();
        return banners.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public BannerDTO toDTO(Banner banner) {
        BannerDTO dto = new BannerDTO();
        BeanUtils.copyProperties(banner, dto);
        Optional<Product> productOpt = productRepository.findById(banner.getProductId());
        productOpt.ifPresent(product -> {
            dto.setProductName(product.getName());
            dto.setProductImage(product.getImage());
        });
        return dto;
    }

    public Banner save(Banner banner) {
        return bannerRepository.save(banner);
    }

    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }

    public Optional<Banner> getById(Long id) {
        return bannerRepository.findById(id);
    }
}
