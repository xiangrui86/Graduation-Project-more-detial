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

    /** 按商家从购物车创建订单，并扣减库存。 */
    @Transactional
    public Order createFromCart(Long userId, Long merchantId, String receiverName, String receiverPhone, String receiverAddress) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        List<CartItem> merchantCart = items.stream().filter(c -> {
            Optional<Product> p = productRepository.findById(c.getProductId());
            return p.isPresent() && p.get().getMerchantId().equals(merchantId);
        }).toList();
        if (merchantCart.isEmpty()) throw new RuntimeException("购物车中无该商家商品");

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem c : merchantCart) {
            Product p = productRepository.findById(c.getProductId()).orElseThrow();
            if (p.getStock() < c.getQuantity()) throw new RuntimeException("商品 " + p.getName() + " 库存不足");
            BigDecimal sub = p.getPrice().multiply(BigDecimal.valueOf(c.getQuantity()));
            total = total.add(sub);
            orderItems.add(OrderItem.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .productImage(p.getImage())
                    .price(p.getPrice())
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

    /** 分页查询商家订单。 */
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
}
