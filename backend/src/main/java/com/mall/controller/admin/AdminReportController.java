package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
/** 管理端报表接口：提供后台首页统计指标和图表数据。 */
public class AdminReportController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    /** 后台看板数据：用户数、商品数、订单数、总销售额。 */
    @GetMapping("/dashboard")
    public ResponseEntity<Result<?>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userRepository.count());
        data.put("productCount", productRepository.count());
        data.put("orderCount", orderRepository.count());
        
        // 计算总销售额（简化版本，实际应该从订单金额累加）
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalRevenue", totalRevenue.setScale(2, BigDecimal.ROUND_HALF_UP));
        
        return ResponseEntity.ok(Result.ok(data));
    }
}
