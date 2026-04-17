package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
/** 管理端订单接口：订单列表与订单详情查询。 */
public class AdminOrderController {

    private final OrderService orderService;

    /** 分页查询全站订单，支持按订单号、状态、用户ID、商家ID过滤。 */
    @GetMapping
    public ResponseEntity<Result<?>> list(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Result.ok(
                orderService.search(orderNo, status, userId, merchantId, PageRequest.of(page, size))));
    }

    /** 查询订单详情（含订单项）。 */
    @GetMapping("/{id}")
    public ResponseEntity<Result<?>> detail(@PathVariable Long id) {
        Optional<Order> order = orderService.getById(id);
        if (order.isEmpty()) return ResponseEntity.ok(Result.fail("订单不存在"));
        List<OrderItem> items = orderService.getItems(id);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order.get());
        data.put("items", items);
        return ResponseEntity.ok(Result.ok(data));
    }

    /** 管理员修改订单状态。 */
    @PostMapping("/{id}/status")
    public ResponseEntity<Result<?>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.ok(Result.fail("请指定订单状态"));
        }
        switch (status) {
            case "PAID", "SHIPPED", "RECEIVED", "CANCELLED", "REFUND_REQUESTED", "REFUNDED" -> orderService.updateStatus(id, status);
            default -> {
                return ResponseEntity.ok(Result.fail("不支持的订单状态"));
            }
        }
        return ResponseEntity.ok(Result.ok(null));
    }
}
