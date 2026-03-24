package com.mall.controller.merchant;

import com.mall.dto.Result;
import com.mall.entity.Product;
import com.mall.entity.User;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/merchant/report")
@RequiredArgsConstructor
/** 商家报表接口：提供看板统计与销量分布数据。 */
public class MerchantReportController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    /** 从当前登录账号解析所属商家 ID。 */
    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非商家账号");
        return u.getMerchantId();
    }

    /** 商家看板数据：商品数、订单数、商品销量扇形图。 */
    @GetMapping("/dashboard")
    public ResponseEntity<Result<?>> dashboard(Authentication auth) {
        Long merchantId = currentMerchantId(auth);
        Map<String, Object> data = new HashMap<>();
        data.put("productCount", productRepository.findByMerchantId(merchantId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        data.put("orderCount", orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId, org.springframework.data.domain.Pageable.unpaged()).getTotalElements());

        // 商品销量扇形图：默认 Top10，其余合并为“其他”
        int topN = 10;
        List<Product> products = productRepository.findByMerchantId(
                        merchantId,
                        PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "sales"))
                )
                .getContent();
        List<Map<String, Object>> pie = new ArrayList<>();
        long othersSales = 0L;
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            long sales = p.getSales() == null ? 0L : p.getSales().longValue();
            if (i < topN) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", p.getName());
                item.put("value", sales);
                pie.add(item);
            } else {
                othersSales += sales;
            }
        }
        if (othersSales > 0) {
            Map<String, Object> other = new HashMap<>();
            other.put("name", "其他");
            other.put("value", othersSales);
            pie.add(other);
        }
        data.put("productSalesPie", pie);

        return ResponseEntity.ok(Result.ok(data));
    }
}
