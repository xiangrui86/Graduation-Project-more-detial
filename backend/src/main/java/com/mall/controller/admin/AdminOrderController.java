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
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Result<?>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Result.ok(orderService.findAll(PageRequest.of(page, size))));
    }

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
}
