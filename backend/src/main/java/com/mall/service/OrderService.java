package com.mall.service;

import com.mall.entity.CartItem;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.Product;
import com.mall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
/** 订单服务：负责下单、查询、状态流转与退款申请。 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /** 按运营从购物车创建订单，并扣减库存。 */
    @Transactional
    public Order createFromCart(Long userId, Long merchantId, String receiverName, String receiverPhone, String receiverAddress) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        List<CartItem> merchantCart = items.stream().filter(c -> {
            Optional<Product> p = productRepository.findById(c.getProductId());
            return p.isPresent() && p.get().getMerchantId().equals(merchantId);
        }).toList();
        if (merchantCart.isEmpty()) throw new RuntimeException("购物车中无该运营商品");

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem c : merchantCart) {
            Product p = productRepository.findById(c.getProductId()).orElseThrow();
            BigDecimal price = p.getPrice();
            
            if (p.getStock() < c.getQuantity()) {
                throw new RuntimeException("商品 " + p.getName() + " 库存不足");
            }
            
            BigDecimal sub = price.multiply(BigDecimal.valueOf(c.getQuantity()));
            total = total.add(sub);
            orderItems.add(OrderItem.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .productImage(p.getImage())
                    .price(price)
                    .quantity(c.getQuantity())
                    .subTotal(sub)
                    .build());
        }

        String orderNo = "O" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + UUID.randomUUID().toString().substring(0, 8);
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .merchantId(merchantId)
                .status("PENDING")
                .totalAmount(total)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .build();
        order = orderRepository.save(order);

        for (OrderItem oi : orderItems) {
            oi.setOrderId(order.getId());
            orderItemRepository.save(oi);
            Product p = productRepository.findById(oi.getProductId()).orElseThrow();
            p.setStock(p.getStock() - oi.getQuantity());
            productRepository.save(p);
        }

        merchantCart.forEach(c -> cartItemRepository.deleteById(c.getId()));
        return order;
    }

    /** 根据订单 ID 查询订单。 */
    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    /** 分页查询用户订单。 */
    public Page<Order> findByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /** 分页查询运营订单。 */
    public Page<Order> findByMerchant(Long merchantId, Pageable pageable) {
        return orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId, pageable);
    }

    /** 分页查询全站订单。 */
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    /** 查询订单明细项。 */
    public List<OrderItem> getItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    /** 更新订单状态。 */
    @Transactional
    public void updateStatus(Long orderId, String status) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        o.setStatus(status);
        orderRepository.save(o);
    }

    /** 收货前取消订单，并回补库存。 */
    @Transactional
    public void cancelOrderByUser(Long orderId, Long userId) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        if (!o.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if ("RECEIVED".equals(o.getStatus()) || "REFUND_REQUESTED".equals(o.getStatus()) || "REFUNDED".equals(o.getStatus())) {
            throw new RuntimeException("当前订单状态不可取消");
        }
        if ("CANCELLED".equals(o.getStatus())) {
            throw new RuntimeException("订单已取消");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product p = productRepository.findById(item.getProductId()).orElse(null);
            if (p == null) continue;
            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            p.setStock((p.getStock() == null ? 0 : p.getStock()) + qty);
            productRepository.save(p);
        }
        o.setStatus("CANCELLED");
        orderRepository.save(o);
    }

    /**
     * 用户申请退货/退款（简化流程）：仅允许对已收货订单发起申请。
     */
    @Transactional
    public void requestRefund(Long orderId, Long userId, String reason) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        if (!o.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if (!"RECEIVED".equals(o.getStatus())) throw new RuntimeException("当前订单状态不可退货");
        if (reason != null && reason.length() > 256) reason = reason.substring(0, 256);

        o.setStatus("REFUND_REQUESTED");
        o.setRefundReason(reason);
        o.setRefundRequestTime(LocalDateTime.now());
        orderRepository.save(o);
    }

    /**
     * 针对单个订单项申请退款；如所有项均已申请，将订单整体标记为 REFUND_REQUESTED。
     */
    @Transactional
    public void requestItemRefund(Long orderId, Long itemId, Long userId, String reason) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        if (!o.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if (!"RECEIVED".equals(o.getStatus()) && !"REFUND_REQUESTED".equals(o.getStatus()))
            throw new RuntimeException("当前订单状态不可申请退款");

        OrderItem item = orderItemRepository.findById(itemId).orElseThrow();
        if (!item.getOrderId().equals(orderId)) throw new RuntimeException("订单项不存在");
        if ("REFUND_REQUESTED".equals(item.getRefundStatus()) || "REFUNDED".equals(item.getRefundStatus()))
            throw new RuntimeException("该商品已申请退款");
        if (reason != null && reason.length() > 256) reason = reason.substring(0, 256);

        item.setRefundStatus("REFUND_REQUESTED");
        item.setRefundReason(reason);
        item.setRefundRequestTime(LocalDateTime.now());
        orderItemRepository.save(item);

        syncOrderRefundStatus(o, orderId);
    }

    /**
     * 批量申请多个订单项退款；支持一次选择多件商品提交。
     */
    @Transactional
    public void requestItemsRefund(Long orderId, List<Long> itemIds, Map<Long, Integer> itemQuantities, Long userId, String reason) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        if (!o.getUserId().equals(userId)) throw new RuntimeException("订单不存在");
        if (!"RECEIVED".equals(o.getStatus()) && !"REFUND_REQUESTED".equals(o.getStatus()))
            throw new RuntimeException("当前订单状态不可申请退款");
        if (itemIds == null || itemIds.isEmpty()) throw new RuntimeException("请至少选择一件商品");
        if (reason != null && reason.length() > 256) reason = reason.substring(0, 256);

        for (Long itemId : itemIds) {
            OrderItem item = orderItemRepository.findById(itemId).orElseThrow();
            if (!item.getOrderId().equals(orderId)) throw new RuntimeException("订单项不存在");
            if ("REFUND_REQUESTED".equals(item.getRefundStatus()) || "REFUNDED".equals(item.getRefundStatus()))
                continue; // 已申请的跳过
            int availableQty = item.getQuantity() == null ? 0 : item.getQuantity();
            int requestQty = itemQuantities != null && itemQuantities.containsKey(itemId)
                    ? itemQuantities.get(itemId)
                    : availableQty;
            if (requestQty <= 0 || requestQty > availableQty) {
                throw new RuntimeException("商品退款数量不合法");
            }
            if (requestQty == availableQty) {
                item.setRefundStatus("REFUND_REQUESTED");
                item.setRefundReason(reason);
                item.setRefundRequestTime(LocalDateTime.now());
                orderItemRepository.save(item);
            } else {
                // 部分数量退款：拆分订单项，原行保留剩余数量，新行标记为退款申请中。
                int remainQty = availableQty - requestQty;
                item.setQuantity(remainQty);
                item.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(remainQty)));
                orderItemRepository.save(item);

                OrderItem refundItem = OrderItem.builder()
                        .orderId(item.getOrderId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .price(item.getPrice())
                        .quantity(requestQty)
                        .subTotal(item.getPrice().multiply(BigDecimal.valueOf(requestQty)))
                        .refundStatus("REFUND_REQUESTED")
                        .refundReason(reason)
                        .refundRequestTime(LocalDateTime.now())
                        .build();
                orderItemRepository.save(refundItem);
            }
        }

        syncOrderRefundStatus(o, orderId);
    }

    /** 若所有订单项均已申请/已退款，将订单整体同步为 REFUND_REQUESTED。 */
    private void syncOrderRefundStatus(Order o, Long orderId) {
        List<OrderItem> allItems = orderItemRepository.findByOrderId(orderId);
        boolean allRequested = allItems.stream().allMatch(i ->
                "REFUND_REQUESTED".equals(i.getRefundStatus()) || "REFUNDED".equals(i.getRefundStatus()));
        if (allRequested) {
            o.setStatus("REFUND_REQUESTED");
            o.setRefundRequestTime(LocalDateTime.now());
            orderRepository.save(o);
        }
    }

    /**
     * 运营同意单个订单项退款；如所有项已退款，订单整体标记为 REFUNDED。
     */
    @Transactional
    public void approveItemRefund(Long orderId, Long itemId, Long merchantId) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        if (!o.getMerchantId().equals(merchantId)) throw new RuntimeException("订单不存在");

        OrderItem item = orderItemRepository.findById(itemId).orElseThrow();
        if (!item.getOrderId().equals(orderId)) throw new RuntimeException("订单项不存在");
        if (!"REFUND_REQUESTED".equals(item.getRefundStatus()))
            throw new RuntimeException("该商品不在退款申请状态");

        item.setRefundStatus("REFUNDED");
        orderItemRepository.save(item);

        // 如所有有退款申请的项均已退款，订单整体标记为 REFUNDED
        List<OrderItem> allItems = orderItemRepository.findByOrderId(orderId);
        boolean hasRequested = allItems.stream().anyMatch(i -> "REFUND_REQUESTED".equals(i.getRefundStatus()));
        boolean hasRefunded  = allItems.stream().anyMatch(i -> "REFUNDED".equals(i.getRefundStatus()));
        if (!hasRequested && hasRefunded) {
            o.setStatus("REFUNDED");
            orderRepository.save(o);
        }
    }
}
