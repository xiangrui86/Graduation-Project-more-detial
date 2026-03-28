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

    public List<BannerDTO> listPublic() {
        List<Banner> banners = bannerRepository.findAllByEnabledTrueOrderBySortOrderAsc();
        return banners.stream()
                .map(this::toPublicDTO)
                .filter(dto -> dto.getProductId() != null && dto.getProductImage() != null)
                .collect(Collectors.toList());
    }

    public BannerDTO toDTO(Banner banner) {
        BannerDTO dto = new BannerDTO();
        BeanUtils.copyProperties(banner, dto);
        Optional<Product> productOpt = productRepository.findById(banner.getProductId());
        productOpt.ifPresent(product -> fillProductInfo(dto, product));
        return dto;
    }

    public BannerDTO toPublicDTO(Banner banner) {
        BannerDTO dto = new BannerDTO();
        BeanUtils.copyProperties(banner, dto);
        Optional<Product> productOpt = productRepository.findPublicById(banner.getProductId());
        productOpt.ifPresent(product -> fillProductInfo(dto, product));
        return dto;
    }

    private void fillProductInfo(BannerDTO dto, Product product) {
        dto.setProductName(product.getName());
        dto.setProductImage(product.getImage());
        dto.setProductDescription(product.getDescription());
        dto.setProductPrice(product.getPrice());
        dto.setProductOriginalPrice(product.getOriginalPrice());
        dto.setProductCategoryName(product.getCategoryId() != null ? String.valueOf(product.getCategoryId()) : "");
        dto.setProductSales(product.getSales());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setImage(product.getImage());
        dto.setSales(product.getSales());
        dto.setCategoryName(product.getCategoryId() != null ? String.valueOf(product.getCategoryId()) : "");
        dto.setImageUrl(product.getImage());
    }

    public Banner save(Banner banner) {
        if (banner.getProductId() != null) {
            Optional<Product> productOpt = productRepository.findById(banner.getProductId());
            productOpt.ifPresent(product -> banner.setImageUrl(product.getImage()));
        }
        return bannerRepository.save(banner);
    }

    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }

    public Optional<Banner> getById(Long id) {
        return bannerRepository.findById(id);
    }
}
