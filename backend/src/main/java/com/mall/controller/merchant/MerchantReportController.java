package com.mall.controller.merchant;

import com.mall.dto.Result;
import com.mall.entity.User;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/merchant/report")
@RequiredArgsConstructor
public class MerchantReportController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非商家账号");
        return u.getMerchantId();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Result<?>> dashboard(Authentication auth) {
        Long merchantId = currentMerchantId(auth);
        Map<String, Object> data = new HashMap<>();
        data.put("productCount", productRepository.findByMerchantId(merchantId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        data.put("orderCount", orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        return ResponseEntity.ok(Result.ok(data));
    }
}
