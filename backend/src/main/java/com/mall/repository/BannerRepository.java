package com.mall.repository;

import com.mall.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByEnabledTrueOrderBySortOrderAsc();
}
