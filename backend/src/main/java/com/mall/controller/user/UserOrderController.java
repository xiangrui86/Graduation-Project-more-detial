package com.mall.controller.user;

import com.mall.dto.Result;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.service.OrderService;
import com.mall.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user/order")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @PostMapping("/create")
    public Result<?> create(Authentication auth, @RequestBody Map<String, Object> body) {
        Long merchantId = Long.valueOf(body.get("merchantId").toString());
        String receiverName = (String) body.get("receiverName");
        String receiverPhone = (String) body.get("receiverPhone");
        String receiverAddress = (String) body.get("receiverAddress");
        try {
            Order order = orderService.createFromCart(currentUserId(auth), merchantId, receiverName, receiverPhone, receiverAddress);
            // 获取该订单的商品名称列表
            List<OrderItem> items = orderService.getItems(order.getId());
            List<String> productNames = items.stream().map(OrderItem::getProductName).toList();
            return Result.ok(productNames);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping
    public Result<?> myOrders(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(orderService.findByUser(currentUserId(auth), PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public Result<?> detail(Authentication auth, @PathVariable Long id) {
        Optional<Order> order = orderService.getById(id);
        if (order.isEmpty() || !order.get().getUserId().equals(currentUserId(auth))) {
            return Result.fail("订单不存在");
        }
        List<OrderItem> items = orderService.getItems(id);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order.get());
        data.put("items", items);
        return Result.ok(data);
    }

    /** 模拟支付：点击支付确认后直接标记为已支付 */
    @PostMapping("/{id}/pay")
    public Result<?> pay(Authentication auth, @PathVariable Long id) {
        boolean ok = paymentService.simulatePay(id, currentUserId(auth));
        return ok ? Result.ok(null) : Result.fail("支付失败");
    }

    @PostMapping("/{id}/confirm-receive")
    public Result<?> confirmReceive(Authentication auth, @PathVariable Long id) {
        Optional<Order> o = orderService.getById(id);
        if (o.isEmpty() || !o.get().getUserId().equals(currentUserId(auth))) {
            return Result.fail("订单不存在");
        }
        orderService.updateStatus(id, "RECEIVED");
        return Result.ok(null);
    }
}
