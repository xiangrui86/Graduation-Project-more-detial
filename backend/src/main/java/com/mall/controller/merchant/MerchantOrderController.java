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
/** 商家端订单接口：订单查询、发货与退款处理。 */
public class MerchantOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    /** 从当前登录账号解析所属商家 ID。 */
    private Long currentMerchantId(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        User u = userRepository.findById(userId).orElseThrow();
        if (u.getMerchantId() == null) throw new RuntimeException("非商家账号");
        return u.getMerchantId();
    }

    /** 分页查询当前商家的订单列表。 */
    @GetMapping
    public ResponseEntity<Result<?>> list(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Result.ok(
                orderService.findByMerchant(currentMerchantId(auth), PageRequest.of(page, size))));
    }

    /** 查询订单详情（含订单项）。 */
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

    /** 商家发货：仅允许已支付订单。 */
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

    /** 同意退款：仅处理退款申请中的订单。 */
    @PostMapping("/{id}/accept-refund")
    public ResponseEntity<Result<?>> acceptRefund(Authentication auth, @PathVariable Long id) {
        Optional<Order> o = orderService.getById(id);
        if (o.isEmpty() || !o.get().getMerchantId().equals(currentMerchantId(auth))) {
            return ResponseEntity.ok(Result.fail("订单不存在"));
        }
        if (!"REFUND_REQUESTED".equals(o.get().getStatus())) {
            return ResponseEntity.ok(Result.fail("订单不在退货申请状态"));
        }
        orderService.updateStatus(id, "REFUNDED");
        return ResponseEntity.ok(Result.ok(null));
    }

    /** 拒绝退款：将订单退回已收货状态。 */
    @PostMapping("/{id}/reject-refund")
    public ResponseEntity<Result<?>> rejectRefund(Authentication auth, @PathVariable Long id) {
        try {
            orderService.rejectRefund(id, currentMerchantId(auth));
            return ResponseEntity.ok(Result.ok(null));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    /** 同意单个订单项退款 */
    @PostMapping("/{orderId}/items/{itemId}/accept-refund")
    public ResponseEntity<Result<?>> acceptItemRefund(
            Authentication auth,
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        try {
            orderService.approveItemRefund(orderId, itemId, currentMerchantId(auth));
            return ResponseEntity.ok(Result.ok(null));
        } catch (Exception e) {
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }
}