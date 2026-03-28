package com.mall.controller.pub;

import com.mall.dto.BannerDTO;
import com.mall.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pub/banner")
@RequiredArgsConstructor
public class PublicBannerController {
    private final BannerService bannerService;

    @GetMapping
    public List<BannerDTO> list() {
        return bannerService.listPublic();
    }
}
