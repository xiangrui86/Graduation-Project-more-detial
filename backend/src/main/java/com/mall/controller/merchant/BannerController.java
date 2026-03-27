package com.mall.controller.merchant;

import com.mall.dto.BannerDTO;
import com.mall.entity.Banner;
import com.mall.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant/banner")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    @GetMapping
    public List<BannerDTO> list() {
        return bannerService.listAll();
    }

    @PostMapping
    public Banner add(@RequestBody Banner banner) {
        return bannerService.save(banner);
    }

    @PutMapping("/{id}")
    public Banner update(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        return bannerService.save(banner);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bannerService.delete(id);
    }
}
