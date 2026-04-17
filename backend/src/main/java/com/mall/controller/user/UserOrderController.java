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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/order")
@RequiredArgsConstructor
/** 用户订单接口：下单、查询、支付、收货与退款申请。 */
public class UserOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    /** 获取当前登录用户 ID。 */
    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    /** 从购物车创建订单。 */
    @PostMapping("/create")
    public Result<?> create(Authentication auth, @RequestBody Map<String, Object> body) {
        Long merchantId = Long.valueOf(body.get("merchantId").toString());
        String receiverName = (String) body.get("receiverName");
        String receiverPhone = (String) body.get("receiverPhone");
        String receiverAddress = (String) body.get("receiverAddress");
        try {
            Order order = orderService.createFromCart(currentUserId(auth), merchantId, receiverName, receiverPhone, receiverAddress);
            // 返回订单ID和订单号
            Map<String, Object> data = new HashMap<>();
            data.put("id", order.getId());
            data.put("orderNo", order.getOrderNo());
            return Result.ok(data);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 分页查询我的订单（含每个订单的商品项）。 */
    @GetMapping
    public Result<?> myOrders(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pagedOrders = orderService.findByUser(currentUserId(auth), PageRequest.of(page, size));
        var enriched = pagedOrders.getContent().stream().map(order -> {
            var map = new java.util.HashMap<String, Object>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("userId", order.getUserId());
            map.put("merchantId", order.getMerchantId());
            map.put("status", order.getStatus());
            map.put("totalAmount", order.getTotalAmount());
            map.put("payAmount", order.getPayAmount());
            map.put("payMethod", order.getPayMethod());
            map.put("payTime", order.getPayTime());
            map.put("receiverName", order.getReceiverName());
            map.put("receiverPhone", order.getReceiverPhone());
            map.put("receiverAddress", order.getReceiverAddress());
            map.put("refundReason", order.getRefundReason());
            map.put("refundRequestTime", order.getRefundRequestTime());
            map.put("refundRejectReason", order.getRefundRejectReason());
            map.put("refundRejectTime", order.getRefundRejectTime());
            map.put("createdAt", order.getCreatedAt());
            map.put("updatedAt", order.getUpdatedAt());
            map.put("items", orderService.getItems(order.getId()));
            return map;
        }).toList();

        return Result.ok(new org.springframework.data.domain.PageImpl<>(
                enriched,
                pagedOrders.getPageable(),
                pagedOrders.getTotalElements()
        ));
    }

    /** 查询订单详情（含订单项）。 */
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
    public Result<?> pay(Authentication auth, @PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        String paymentMethod = body != null ? (String) body.get("paymentMethod") : null;
        if (paymentMethod == null) {
            paymentMethod = "WECHAT";
        }
        boolean ok = paymentService.simulatePay(id, currentUserId(auth), paymentMethod);
        return ok ? Result.ok(null) : Result.fail("支付失败");
    }

    /** 用户确认收货。 */
    @PostMapping("/{id}/confirm-receive")
    public Result<?> confirmReceive(Authentication auth, @PathVariable Long id) {
        Optional<Order> o = orderService.getById(id);
        if (o.isEmpty() || !o.get().getUserId().equals(currentUserId(auth))) {
            return Result.fail("订单不存在");
        }
        orderService.updateStatus(id, "RECEIVED");
        return Result.ok(null);
    }

    /** 完成订单（评价后）。 */
    @PostMapping("/{id}/complete")
    public Result<?> complete(Authentication auth, @PathVariable Long id) {
        Optional<Order> o = orderService.getById(id);
        if (o.isEmpty() || !o.get().getUserId().equals(currentUserId(auth))) {
            return Result.fail("订单不存在");
        }
        orderService.updateStatus(id, "COMPLETED");
        return Result.ok(null);
    }

    /** 收货前取消订单。 */
    @PostMapping("/{id}/cancel")
    public Result<?> cancel(Authentication auth, @PathVariable Long id) {
        try {
            orderService.cancelOrderByUser(id, currentUserId(auth));
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 申请退货/退款（简化流程） */
    @PostMapping("/{id}/refund-request")
    public Result<?> refundRequest(Authentication auth, @PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        String reason = body == null ? null : (String) body.get("reason");
        orderService.requestRefund(id, currentUserId(auth), reason);
        return Result.ok(null);
    }

    /** 针对单个订单项申请退款 */
    @PostMapping("/{orderId}/items/{itemId}/refund-request")
    public Result<?> itemRefundRequest(
            Authentication auth,
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody(required = false) Map<String, Object> body) {
        String reason = body == null ? null : (String) body.get("reason");
        try {
            orderService.requestItemRefund(orderId, itemId, currentUserId(auth), reason);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /** 批量申请多个订单项退款 */
    @PostMapping("/{orderId}/items/batch-refund-request")
    public Result<?> batchItemRefundRequest(
            Authentication auth,
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> body) {
        String reason = (String) body.get("reason");
        List<Long> itemIds = ((List<?>) body.get("itemIds")).stream()
                .map(id -> Long.valueOf(id.toString()))
                .collect(Collectors.toList());
        Map<Long, Integer> itemQuantities = new HashMap<>();
        Object quantitiesObj = body.get("itemQuantities");
        if (quantitiesObj instanceof Map<?, ?> quantities) {
            for (Map.Entry<?, ?> e : quantities.entrySet()) {
                if (e.getKey() == null || e.getValue() == null) continue;
                Long itemId = Long.valueOf(e.getKey().toString());
                Integer qty = Integer.valueOf(e.getValue().toString());
                itemQuantities.put(itemId, qty);
            }
        }
        try {
            orderService.requestItemsRefund(orderId, itemIds, itemQuantities, currentUserId(auth), reason);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
