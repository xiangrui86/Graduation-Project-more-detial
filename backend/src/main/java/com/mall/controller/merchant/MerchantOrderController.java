package com.mall.controller.merchant;

import com.mall.dto.Result;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.User;
import com.mall.repository.UserRepository;
import com.mall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/merchant/order")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非商家账号");
        return u.getMerchantId();
    }

    @GetMapping
    public ResponseEntity<Result<?>> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Result.ok(
                orderService.findByMerchant(currentMerchantId(auth), PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<?>> detail(Authentication auth, @PathVariable Long id) {
        Optional<Order> order = orderService.getById(id);
        if (order.isEmpty() || !order.get().getMerchantId().equals(currentMerchantId(auth))) {
            return ResponseEntity.ok(Result.fail("订单不存在"));
        }
        List<OrderItem> items = orderService.getItems(id);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order.get());
        data.put("items", items);
        return ResponseEntity.ok(Result.ok(data));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<Result<?>> ship(Authentication auth, @PathVariable Long id) {
        Optional<Order> o = orderService.getById(id);
        if (o.isEmpty() || !o.get().getMerchantId().equals(currentMerchantId(auth))) {
            return ResponseEntity.ok(Result.fail("订单不存在"));
        }
        if (!"PAID".equals(o.get().getStatus())) return ResponseEntity.ok(Result.fail("订单未支付"));
        orderService.updateStatus(id, "SHIPPED");
        return ResponseEntity.ok(Result.ok(null));
    }
}