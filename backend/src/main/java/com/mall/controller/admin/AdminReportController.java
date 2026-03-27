package com.mall.controller.admin;

import com.mall.common.Role;
import com.mall.dto.Result;
import com.mall.entity.Order;
import com.mall.entity.Product;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
        
        // 1. 用户总数：只统计普通用户（role=USER）
        long userCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.USER)
                .count();
        data.put("userCount", userCount);
        
        // 2. 商品总数：只统计上架的商品（onSale=true）
        long productCount = productRepository.findAll().stream()
                .filter(Product::getOnSale)
                .count();
        data.put("productCount", productCount);
        
        // 3. 订单总数：统计已支付及以后状态的订单
        long orderCount = orderRepository.findAll().stream()
                .filter(o -> !o.getStatus().equals("PENDING") && !o.getStatus().equals("CANCELLED"))
                .count();
        data.put("orderCount", orderCount);
        
        // 4. 总销售额：计算已支付订单的总额
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> !o.getStatus().equals("PENDING") && !o.getStatus().equals("CANCELLED"))
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        data.put("totalRevenue", totalRevenue);
        
        // 5. 最近7天订单数
        data.put("orderTrend", getOrderTrendData());
        
        // 6. 分类销售占比
        data.put("categorySales", getCategorySalesData());
        
        // 7. 用户增长趋势（最近6个月）
        data.put("userGrowth", getUserGrowthData());
        
        // 8. 商品状态分布
        data.put("productStatus", getProductStatusData());
        
        return ResponseEntity.ok(Result.ok(data));
    }
    
    /** 最近7天订单数 */
    private List<Integer> getOrderTrendData() {
        List<Integer> trend = new ArrayList<>();
        List<Order> allOrders = orderRepository.findAll();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            
            long count = allOrders.stream()
                    .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().toLocalDate().equals(date))
                    .filter(o -> !o.getStatus().equals("PENDING") && !o.getStatus().equals("CANCELLED"))
                    .count();
            trend.add((int) count);
        }
        return trend;
    }
    
    /** 分类销售占比 */
    private List<Map<String, Object>> getCategorySalesData() {
        Map<Long, Integer> categorySalesMap = new HashMap<>();
        Map<Long, String> categoryNameMap = new HashMap<>();
        
        // 统计各分类的销售额
        List<Product> allProducts = productRepository.findAll();
        for (Product product : allProducts) {
            if (product.getCategoryId() != null) {
                int value = product.getSales() != null ? product.getSales() : 0;
                categorySalesMap.put(
                    product.getCategoryId(),
                    categorySalesMap.getOrDefault(product.getCategoryId(), 0) + value
                );
                // 简单映射分类ID到名称（实际应该从Category表获取）
                categoryNameMap.put(product.getCategoryId(), "分类" + product.getCategoryId());
            }
        }
        
        // 转换为所需格式
        return categorySalesMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", categoryNameMap.get(e.getKey()));
                    map.put("value", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    /** 用户增长趋势（最近6个月） */
    private List<Integer> getUserGrowthData() {
        List<Integer> growth = new ArrayList<>();
        List<Long> allUserIds = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.USER)
                .map(u -> u.getId())
                .collect(Collectors.toList());
        
        for (int i = 5; i >= 0; i--) {
            LocalDate date = LocalDate.now().withDayOfMonth(1).minusMonths(i);
            LocalDateTime endOfMonth = date.plusMonths(1).atStartOfDay().minusSeconds(1);
            
            long count = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.USER)
                    .filter(u -> u.getCreatedAt().isBefore(endOfMonth))
                    .count();
            growth.add((int) count);
        }
        return growth;
    }
    
    /** 商品状态分布 */
    private Map<String, Integer> getProductStatusData() {
        Map<String, Integer> status = new HashMap<>();
        List<Product> allProducts = productRepository.findAll();
        
        // 销售中：onSale=true,stock>0
        int onSale = (int) allProducts.stream()
                .filter(p -> p.getOnSale() && p.getStock() > 0)
                .count();
        
        // 已售罄：onSale=true,stock=0
        int outOfStock = (int) allProducts.stream()
                .filter(p -> p.getOnSale() && p.getStock() == 0)
                .count();
        
        // 已下架：onSale=false
        int offline = (int) allProducts.stream()
                .filter(p -> !p.getOnSale())
                .count();
        
        status.put("销售中", onSale);
        status.put("已售罄", outOfStock);
        status.put("已下架", offline);
        
        return status;
    }
}
